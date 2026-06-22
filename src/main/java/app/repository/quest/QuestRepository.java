package app.repository.quest;

import app.model.entity.quest.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestRepository extends JpaRepository<Quest, UUID> {
    boolean existsByTitle(String title);

    Optional<Quest> findByTitle(String title);
}
