package beat.mercy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import beat.mercy.entity.Customer;
import beat.mercy.entity.FrontStaff;
import beat.mercy.entity.base.BaseCarService;
import beat.mercy.entity.rbac.Authority;
import beat.mercy.entity.rbac.Role;
import beat.mercy.repository.AuthorityRepository;
import beat.mercy.repository.CarServiceRepository;
import beat.mercy.repository.CustomerRepository;
import beat.mercy.repository.FrontStaffRepository;
import beat.mercy.repository.RoleRepository;
import beat.mercy.repository.StaffRepository;

@SpringBootApplication
@RestController
@EnableScheduling // 启用定时任务
public class EasyCarServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EasyCarServiceApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public CommandLineRunner runWith(
			CarServiceRepository carServiceRepository,
			AuthorityRepository authorityRepository,
			RoleRepository roleRepository,
			FrontStaffRepository frontStaffRepository,
			CustomerRepository customerRepository,
			BCryptPasswordEncoder bCryptPsswordEncoder,
			StaffRepository staffRepository) {
		return(args) -> {
			// 设权
			authorityRepository.save( new Authority("NORMAL_USER"));
			authorityRepository.save(new Authority("NORMAL_STAFF"));
			authorityRepository.save(new Authority("NORMAL_ADMIN"));
			
			Set<Authority> userASet = new HashSet<>();
			Set<Authority> staffASet = new HashSet<>();
			Set<Authority> adminASet = new HashSet<>();
			userASet.add(authorityRepository.findByName("NORMAL_USER"));
			staffASet.addAll(
					Arrays.asList(authorityRepository.findByName("NORMAL_STAFF"),
					authorityRepository.findByName("NORMAL_USER"))
					);
			adminASet.addAll(
					Arrays.asList(authorityRepository.findByName("NORMAL_ADMIN"),
					authorityRepository.findByName("NORMAL_USER")));
			// 设角色
			roleRepository.save(new Role("ROLE_USER",userASet));
			roleRepository.save(new Role("ROLE_STAFF",staffASet));
			roleRepository.save(new Role("ROLE_ADMIN",adminASet));
			
			Customer customer = new Customer();
			customer.setUsername("mercy");
			customer.setPassword(bCryptPasswordEncoder().encode("123456"));
			customer.getRoles().add(roleRepository.findByName("ROLE_USER"));
			
			FrontStaff staff = new FrontStaff();
			staff.setUsername("frontStaff1");
			staff.setComNom("01");
			staff.setPassword(bCryptPasswordEncoder().encode("123456"));
			staff.getRoles().add(roleRepository.findByName("ROLE_STAFF"));
			
			BaseCarService baseCs0 = new BaseCarService();
			baseCs0.setName("小型车洗车服务");
			baseCs0.setDescription("为车型在xxxx一下的车提供洗车服务");
			baseCs0.setStartPrice(35.0d);
			
			BaseCarService baseCs1 = new BaseCarService();
			baseCs1.setName("小型车保养服务");
			baseCs1.setDescription("为车型在xxxx一下的车提供洗车服务");
			baseCs1.setStartPrice(435.0d);
			
			BaseCarService baseCs2 = new BaseCarService();
			baseCs2.setName("大型车洗车服务");
			baseCs2.setDescription("为车型在xxxx一下的车提供洗车服务");
			baseCs2.setStartPrice(655.0d);
			carServiceRepository.save(Arrays.asList(baseCs0,baseCs1,baseCs2));
			BaseCarService selected = carServiceRepository.findOne(2L);
			selected.setName("小型车保养服务[优惠]");
			carServiceRepository.save(selected);
			
			customerRepository.save(customer);
			frontStaffRepository.save(staff);
			
			staffRepository.findAll().forEach(child->{
				System.err.println(child.getUsername());
				System.err.println(child.getType());
				child.getRoles().forEach(role->{
					System.err.println(role.getName());
				});
			});
			
		};
	}
}

