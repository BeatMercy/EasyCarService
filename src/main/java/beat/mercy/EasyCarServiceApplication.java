package beat.mercy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import beat.mercy.entity.Administrator;
import beat.mercy.entity.Advertisement;
import beat.mercy.entity.CarBeautifyCombo;
import beat.mercy.entity.Customer;
import beat.mercy.entity.Staff;
import beat.mercy.entity.Vehicle;
import beat.mercy.entity.base.BaseCarService;
import beat.mercy.entity.option.SelectOption;
import beat.mercy.entity.rbac.Authority;
import beat.mercy.entity.rbac.Role;
import beat.mercy.entity.state.FuseType;
import beat.mercy.entity.state.VehicleType;
import beat.mercy.repository.AccountRepository;
import beat.mercy.repository.AdministratorRepository;
import beat.mercy.repository.AdvertisementRepository;
import beat.mercy.repository.AuthorityRepository;
import beat.mercy.repository.CarServiceRepository;
import beat.mercy.repository.RoleRepository;
import beat.mercy.repository.SelectOptionRepository;
import beat.mercy.repository.VehicleRepository;

@SpringBootApplication
@RestController
@EnableScheduling // 启用定时任务
@EnableCaching
public class EasyCarServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EasyCarServiceApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

//	@Bean
	public CommandLineRunner runWith(AuthorityRepository authorityRepository, RoleRepository roleRepository,
			BCryptPasswordEncoder bCryptPsswordEncoder, AdministratorRepository adminRepository,VehicleRepository vehicleRepo,
			AccountRepository accountRepository) {
		return (args) -> {
			// 设权
			authorityRepository.save(new Authority("ROLE_USER"));
			authorityRepository.save(new Authority("ROLE_STAFF"));
			authorityRepository.save(new Authority("ROLE_ADMIN"));

			Set<Authority> userASet = new HashSet<>();
			Set<Authority> staffASet = new HashSet<>();
			Set<Authority> adminASet = new HashSet<>();
			userASet.add(authorityRepository.findByName("ROLE_USER"));
			staffASet.addAll(Arrays.asList(authorityRepository.findByName("ROLE_STAFF"),
					authorityRepository.findByName("ROLE_USER")));
			adminASet.addAll(Arrays.asList(authorityRepository.findByName("ROLE_ADMIN"),
					authorityRepository.findByName("ROLE_USER")));
			// 设角色
			roleRepository.save(new Role("ROLE_USER", userASet));
			roleRepository.save(new Role("ROLE_STAFF", staffASet));
			roleRepository.save(new Role("ROLE_ADMIN", adminASet));

			Customer customer = new Customer();
			customer.setUsername("mercy");
			customer.setPassword(bCryptPasswordEncoder().encode("123456"));
			customer.getRoles().add(roleRepository.findByName("ROLE_USER"));
			customer.setRealName("吴博闻");
			customer.setHeadimg("assets/img/beatMercy.png");
			
			
			
			Staff staff = new Staff();
			staff.setUsername("frontStaff1");
			staff.setPassword(bCryptPasswordEncoder().encode("123456"));
			staff.getRoles().add(roleRepository.findByName("ROLE_STAFF"));

			Administrator admin = new Administrator();
			admin.setUsername("admin");
			admin.setPassword(bCryptPasswordEncoder().encode("123456"));
			admin.getRoles().add(roleRepository.findByName("ROLE_ADMIN"));
			admin.getRoles().add(roleRepository.findByName("ROLE_STAFF"));
			admin.getRoles().add(roleRepository.findByName("ROLE_USER"));
			admin.setHeadimg("assets/img/beatMercy.png");

			
			Vehicle v = new Vehicle();
			v.setPlateNo("粤S63B87");
			v.setBrand("丰田");
			v.setFuseType(FuseType.PETROL92);
			v.setVehicleType(VehicleType.SMALL);
			v.setOwner(customer);
			customer.getVehicles().add(v);
			accountRepository.save(customer);
			accountRepository.save(staff);
			accountRepository.save(admin);
			
			

		};
	}

	/**
	 * 服务初始化 author: Mercy Wu(a3049) 2018年4月2日 上午11:02:26
	 * 
	 * @param carServiceRepository
	 * @return
	 */
//	@Bean
	public CommandLineRunner initializeService(CarServiceRepository carServiceRepository) {
		return (args) -> {

			BaseCarService baseCs0 = new BaseCarService();
			baseCs0.setName("小型车洗车服务");
			baseCs0.setTags("洗车");
			baseCs0.setDescription("为车型在xxxx一下的车提供洗车服务");
			baseCs0.setPrice(35.0d);

			BaseCarService baseCs1 = new BaseCarService();
			baseCs1.setName("小型车保养服务[优惠]");
			baseCs1.setTags("保养");
			baseCs1.setDescription("为车型在xxxx一下的车提供洗车服务");
			baseCs1.setPrice(435.0d);

			CarBeautifyCombo baseCs2 = new CarBeautifyCombo();
			baseCs2.setName("大型车洗车服务");
			baseCs2.setTags("洗车");
			baseCs2.setDescription("为车型在xxxx一下的车提供洗车服务");
			baseCs2.setPrice(85.0d);

			carServiceRepository.save(Arrays.asList(baseCs0, baseCs1, baseCs2));
		};
	}

	/**
	 * 后台内容初始化 author: Mercy Wu(a3049) 2018年4月2日 上午11:04:16
	 * 
	 * @param advertisementRepository
	 * @return
	 */
//	@Bean
	public CommandLineRunner initializeMGModual(AdvertisementRepository advertisementRepository,SelectOptionRepository optionRepository) {
		return (args) -> {

			// 广告模块
			Advertisement ad = new Advertisement();
			ad.setTitle("测试的广告");
			ad.setDescription("今日特价：搜索热门洗车");
			ad.setImgUrl("/img/homeAd/1");
			ad.setSequence(1);
			ad.setIsShow(true);
			Advertisement ad2 = new Advertisement();
			ad2.setTitle("测试的广告2");
			ad2.setDescription("今日特价：搜索热门保养");
			ad2.setImgUrl("/img/homeAd/2");
			ad2.setSequence(1);
			ad2.setIsShow(true);

			
			// 选项
			SelectOption option =new SelectOption();
			option.setName("清洗选项");
			option.setServiceType(SelectOption.CAR_BEAUTIFY_SERVICE);
			option.setOptionType(SelectOption.SHAMPOO_OPTION);
			option.setItemName("外观清洗：普通清洗");
			option.setPrice(35.2);
			
			SelectOption option2 =new SelectOption();
			option2.setName("清洗选项");
			option2.setServiceType(SelectOption.CAR_BEAUTIFY_SERVICE);
			option2.setOptionType(SelectOption.SHAMPOO_OPTION);
			option2.setItemName("外观清洗：高级清洗");
			option2.setPrice(60.00);
			
			SelectOption option3 =new SelectOption();
			option3.setName("打蜡选项");
			option3.setServiceType(SelectOption.CAR_BEAUTIFY_SERVICE);
			option3.setOptionType(SelectOption.WAX_OPTION);
			option3.setItemName("打蜡：普通蜡");
			option3.setPrice(45.00);
			
			SelectOption option4 =new SelectOption();
			option4.setName("内饰清洗选项");
			option4.setServiceType(SelectOption.CAR_BEAUTIFY_SERVICE);
			option4.setOptionType(SelectOption.INTERNAL_WASH_OPTION);
			option4.setItemName("内饰清洗：普通清洗");
			option4.setPrice(35.2);
			
			SelectOption option5 =new SelectOption();
			option5.setName("机油滤嘴选项");
			option5.setServiceType(SelectOption.CAR_MAINTENANCE_SERVICE);
			option5.setOptionType(SelectOption.ENGINE_FILTER_OPTION);
			option5.setItemName("机油滤嘴：普通");
			option5.setPrice(35.2);
			
			SelectOption option6 =new SelectOption();
			option6.setName("机油选项");
			option6.setServiceType(SelectOption.CAR_MAINTENANCE_SERVICE);
			option6.setOptionType(SelectOption.ENGINE_OIL_OPTION);
			option6.setItemName("机油：嘉实多");
			option6.setPrice(60.00);
			
			SelectOption option7 =new SelectOption();
			option6.setName("机油选项");
			option7.setServiceType(SelectOption.CAR_MAINTENANCE_SERVICE);
			option7.setOptionType(SelectOption.ENGINE_OIL_OPTION);
			option7.setItemName("机油：美孚");
			option7.setPrice(45.00);
			
			SelectOption option8 =new SelectOption();
			option6.setName("刹车油选项");
			option8.setServiceType(SelectOption.CAR_MAINTENANCE_SERVICE);
			option8.setOptionType(SelectOption.BREAK_OIL_OPTION);
			option8.setItemName("刹车油：嘉实多");
			option8.setPrice(35.2);
			
			
			optionRepository.save(Arrays.asList(option,option2,option3,option4,option5,option6,option7,option8));
			advertisementRepository.save(Arrays.asList(ad, ad2));
		};
	}

}
