package beat.mercy.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import beat.mercy.common.util.FileUtil;
import beat.mercy.entity.option.SelectOption;
import beat.mercy.repository.SelectOptionRepository;

/**
 * 网站里相对固定数据的返回
 * 
 * @author Mercy Wu(a3049) 2018年4月4日
 */
@RestController
public class BasicDataController {

	@Autowired
	private SelectOptionRepository optionRepo;

	@RequestMapping("/img/{type}/{id}")
	public ResponseEntity<byte[]> imgRetrieve(@PathVariable(name = "type") String type,
			@PathVariable(name = "id") String id) throws IOException {
		String filePath = FileUtil.UPLOAD_ROOT_PATH + "/" + type + "/" + id;
		try {
			return FileUtil.downloadFile(filePath);
		} catch (Exception e) {
			if(type.equals("brand")) {
				return FileUtil.downloadFile(FileUtil.UPLOAD_ROOT_PATH + "/" + type + "/default");
			}
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping("/serviceOptions")
	public List<SelectOption> optionRetrieve() {
		return optionRepo.findAll();
	}

	@RequestMapping("/serviceOption/{serviceType}/{optionType}")
	public List<SelectOption> optionRetrieve(@PathVariable("serviceType") String serviceType,
			@PathVariable("optionType") String optionType) {

		if (optionType.equals("all"))
			return optionRepo.findByServiceTypeAndEnable(serviceType,true);
		return optionRepo.findByServiceTypeAndOptionTypeAndEnable(serviceType, optionType, true);
	}

	@RequestMapping("/isActiveAccount")
	public String isActiveAccount() {
		return "{\"success\":true}";
	}

}
