package app.init;

import app.model.dto.user.RegisterDTO;
import app.model.entity.hero.Hero;
import app.model.entity.hero.HeroClass;
import app.model.entity.user.Role;
import app.model.entity.user.Server;
import app.model.entity.user.User;
import app.repository.hero.HeroRepository;
import app.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserInit implements CommandLineRunner {
    private final UserRepository userRepository;
    private final HeroRepository heroRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserInit(UserRepository userRepository, HeroRepository heroRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.heroRepository = heroRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.count() > 0){
            return;
        }

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@kingdomquest.com")
                .profilePicture("/images/warrior.jpeg")
                .role(Role.ADMIN)
                .server(Server.EUROPE)
                .isActive(true)
                .build();

        Hero hero = Hero.builder()
                .roleplayName("The Administrator")
                .heroClass(HeroClass.WARRIOR)
                .level(100)
                .xp(9900)
                .gold(99999)
                .user(admin)
                .build();

        admin.setHero(hero);
        userRepository.save(admin);
        heroRepository.save(hero);

        log.info("Default admin user created");
    }
}
