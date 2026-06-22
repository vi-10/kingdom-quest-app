package app.model.dto.quest;

import app.model.entity.quest.QuestType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class QuestDTO {

    private UUID id;
    private String title;
    private String description;
    private QuestType questType;
    private int requiredLevel;
    private int rewardXp;
    private int rewardGold;
}
