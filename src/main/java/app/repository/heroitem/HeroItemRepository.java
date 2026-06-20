package app.repository.heroitem;

import app.model.entity.heroitem.HeroItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HeroItemRepository extends JpaRepository<HeroItem, UUID> {
}
