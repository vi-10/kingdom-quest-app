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

    @NotNull(message = "Required level must not be null.")
    @Min(value = 1, message = "Required level must be at least 1.")
    private Integer requiredLevel;

    @NotNull(message = "Reward XP must not be null.")
    @Min(value = 0, message = "Reward XP cannot be negative.")
    private Integer rewardXp;

    @NotNull(message = "Reward gold must not be null.")
    @Min(value = 0, message = "Reward gold cannot be negative.")
    private Integer rewardGold;

    @NotNull(message = "Quest type must not be null.")
    private QuestType questType;
}
