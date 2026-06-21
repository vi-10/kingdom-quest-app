package app.service.quest;

import app.mapper.quest.QuestMapper;
import app.model.dto.quest.QuestDTO;
import app.repository.quest.QuestRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class QuestService {
    private QuestRepository questRepository;

    @Autowired
    public QuestService(QuestRepository questRepository) {
        this.questRepository = questRepository;
    }

    public List<QuestDTO> getAllQuests() {
        return questRepository.findAll().stream().map(QuestMapper::toQuestDTO).toList();
    }
}
