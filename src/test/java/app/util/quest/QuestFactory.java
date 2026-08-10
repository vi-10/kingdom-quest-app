package app.util.quest;

import app.model.entity.quest.Quest;
import app.model.entity.quest.QuestType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class QuestFactory {
    public static Quest getQuest(){
        return Quest.builder()
                .title("Defeat the Goblin")
                .description("Defeat a dangerous goblin.")
                .questType(QuestType.COMBAT)
                .requiredLevel(1)
                .rewardXp(80)
                .rewardGold(50)
                .build();
    }
}
