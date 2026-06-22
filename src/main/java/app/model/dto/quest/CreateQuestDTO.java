package app.model.dto.quest;

import app.model.entity.quest.QuestType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateQuestDTO {
    @NotBlank(message = "Quest title must not be blank.")
    private String title;

    @NotBlank(message = "Description must not be blank.")
    private String description;

    @Min(value = 1, message = "Required level must be at least 1.")
    private int requiredLevel;

    @Min(value = 0, message = "Reward XP cannot be negative.")
    private int rewardXp;

    @Min(value = 0, message = "Reward gold cannot be negative.")
    private int rewardGold;

    @NotNull(message = "Quest type must not be null.")
    private QuestType questType;
}
