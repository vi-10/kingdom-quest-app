package app.service.hero;

import app.exception.UserNotFoundException;
import app.mapper.hero.HeroMapper;
import app.model.dto.hero.HeroDTO;
import app.model.entity.hero.Hero;
import app.repository.hero.HeroRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
public class HeroService {
    private HeroRepository heroRepository;

    @Autowired
    public HeroService(HeroRepository heroRepository) {
        this.heroRepository = heroRepository;
    }

    public HeroDTO getByUserId(UUID userId) {
        Hero hero = heroRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException(String.format("User with %s id doesn't exist.", userId)));
        return HeroMapper.toHeroDTO(hero);
    }
}
