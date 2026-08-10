package app.service.hero;

import app.exception.HeroNotFoundException;
import app.mapper.hero.HeroMapper;
import app.model.dto.hero.HeroDTO;
import app.model.entity.hero.Hero;
import app.repository.hero.HeroRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@Transactional
public class HeroService {
    private HeroRepository heroRepository;

    @Autowired
    public HeroService(HeroRepository heroRepository) {
        this.heroRepository = heroRepository;
    }

    public HeroDTO getByUserId(UUID userId) {
        log.debug("Fetching hero for user {}", userId);

        Hero hero = heroRepository.findByUserId(userId).orElseThrow(HeroNotFoundException::new);

        log.debug("Hero found for user {}", userId);

        return HeroMapper.toHeroDTO(hero);
    }
}
