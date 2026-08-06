package app.model.dto.event;

import app.model.entity.quest.QuestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveEventResponse {

    private String title;
    private String description;
    private QuestType affectedQuestType;
    private int bonusXp;
    private int bonusGold;
    private LocalDateTime start;
    private LocalDateTime end;

}
