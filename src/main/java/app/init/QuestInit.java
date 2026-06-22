package app.init;

import app.model.entity.quest.Quest;
import app.model.entity.quest.QuestType;
import app.repository.quest.QuestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class QuestInit implements CommandLineRunner {
    private QuestRepository questRepository;

    @Autowired
    public QuestInit(QuestRepository questRepository) {
        this.questRepository = questRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (questRepository.count() > 0) {
            return;
        }

        Quest q1 = Quest.builder()
                .title("Rat Extermination")
                .description("The village sewers are overrun with rats. Clear them out.")
                .questType(QuestType.COMBAT)
                .requiredLevel(1)
                .rewardXp(50)
                .rewardGold(20)
                .build();

        Quest q2 = Quest.builder()
                .title("Lost Herbs")
                .description("Collect healing herbs from the nearby forest for the local healer.")
                .questType(QuestType.SUPPORT)
                .requiredLevel(1)
                .rewardXp(45)
                .rewardGold(25)
                .build();

        Quest q3 = Quest.builder()
                .title("Broken Relic Recovery")
                .description("Retrieve a stolen relic from bandits hiding near the road.")
                .questType(QuestType.STEALTH)
                .requiredLevel(1)
                .rewardXp(55)
                .rewardGold(30)
                .build();

        Quest q4 = Quest.builder()
                .title("Arcane Sparks")
                .description("Stabilize unstable magical energy leaking from an old shrine.")
                .questType(QuestType.MAGIC)
                .requiredLevel(1)
                .rewardXp(60)
                .rewardGold(35)
                .build();

        Quest q5 = Quest.builder()
                .title("Bandit Ambush")
                .description("Eliminate a group of organized bandits threatening trade routes.")
                .questType(QuestType.COMBAT)
                .requiredLevel(2)
                .rewardXp(90)
                .rewardGold(60)
                .build();

        Quest q6 = Quest.builder()
                .title("Cursed Village")
                .description("Investigate a village suffering from a mysterious curse.")
                .questType(QuestType.MAGIC)
                .requiredLevel(2)
                .rewardXp(100)
                .rewardGold(70)
                .build();

        Quest q7 = Quest.builder()
                .title("Silent Infiltration")
                .description("Infiltrate a noble estate and gather confidential documents.")
                .questType(QuestType.STEALTH)
                .requiredLevel(2)
                .rewardXp(95)
                .rewardGold(80)
                .build();

        Quest q8 = Quest.builder()
                .title("Wounded Soldiers")
                .description("Heal injured soldiers after a border clash.")
                .questType(QuestType.SUPPORT)
                .requiredLevel(2)
                .rewardXp(85)
                .rewardGold(65)
                .build();

        questRepository.saveAll(List.of(q1, q2, q3, q4, q5, q6, q7, q8));
        log.info("Default quests created");

    }
}
