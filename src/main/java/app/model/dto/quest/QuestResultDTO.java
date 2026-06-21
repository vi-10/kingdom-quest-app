package app.model.dto.quest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestResultDTO {

    private boolean success;
    private String message;
}
