package app.mapper.quest;

import app.model.dto.quest.QuestDTO;
import app.model.entity.quest.Quest;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class QuestMapper {
    public static QuestDTO toQuestDTO(Quest quest){
        if (quest == null) {
            return null;
        }

        return QuestDTO.builder()
                .id(quest.getId())
                .title(quest.getTitle())
                .description(quest.getDescription())
                .questType(quest.getQuestType())
                .requiredLevel(quest.getRequiredLevel())
                .rewardXp(quest.getRewardXp())
                .rewardGold(quest.getRewardGold())
                .build();
    }
}
