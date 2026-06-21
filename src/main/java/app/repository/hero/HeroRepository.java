package app.repository.hero;

import app.model.entity.hero.Hero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HeroRepository extends JpaRepository<Hero, UUID> {

    Optional<Hero> findByUserId(UUID userId);
}
