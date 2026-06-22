package app.service.quest;

import app.exception.QuestAlreadyExistsException;
import app.exception.QuestNotFoundException;
import app.exception.UserNotFoundException;
import app.mapper.quest.QuestMapper;
import app.model.dto.quest.CreateQuestDTO;
import app.model.dto.quest.EditQuestDTO;
import app.model.dto.quest.QuestDTO;
import app.model.dto.quest.QuestResultDTO;
import app.model.entity.hero.Hero;
import app.model.entity.hero.HeroClass;
import app.model.entity.quest.Quest;
import app.model.entity.quest.QuestType;
import app.repository.hero.HeroRepository;
import app.repository.quest.QuestRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class QuestService {
    private QuestRepository questRepository;
    private HeroRepository heroRepository;

    @Autowired
    public QuestService(QuestRepository questRepository, HeroRepository heroRepository) {
        this.questRepository = questRepository;
        this.heroRepository = heroRepository;
    }

    public List<QuestDTO> getAllQuests() {
        return questRepository.findAll().stream().map(QuestMapper::toQuestDTO).toList();
    }

    public QuestResultDTO completeQuest(UUID id, UUID userId) {
        Hero hero = heroRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Hero not found"));

        Quest quest = questRepository.findById(id)
                .orElseThrow(() -> new QuestNotFoundException("Quest not found"));

        if (hero.getHeroClass() == HeroClass.WARRIOR && quest.getQuestType() != QuestType.COMBAT
                || hero.getHeroClass() == HeroClass.MAGE && quest.getQuestType() != QuestType.MAGIC
                || hero.getHeroClass() == HeroClass.ROGUE && quest.getQuestType() != QuestType.STEALTH
                || hero.getHeroClass() == HeroClass.HEALER && quest.getQuestType() != QuestType.SUPPORT) {
            return QuestResultDTO.builder()
                    .success(false)
                    .message("Your hero class cannot complete this quest.")
                    .build();
        }

        if (hero.getLevel() < quest.getRequiredLevel()) {
            return QuestResultDTO.builder()
                    .success(false)
                    .message("Your level is too low for this quest.")
                    .build();
        }

        hero.setXp(hero.getXp() + quest.getRewardXp());
        hero.setLevel(hero.getXp() / 100 + 1);
        hero.setGold(hero.getGold() + quest.getRewardGold());

        heroRepository.save(hero);

        return QuestResultDTO.builder()
                .success(true)
                .message("You earned " + quest.getRewardXp() + " XP and " +
                        quest.getRewardGold() + " gold!")
                .build();
    }

    public void createQuest(CreateQuestDTO questData) {
        if (questRepository.existsByTitle(questData.getTitle())) {
            throw new QuestAlreadyExistsException(
                    "A quest with this title already exists"
            );
        }

        Quest quest = Quest.builder()
                .title(questData.getTitle())
                .description(questData.getDescription())
                .requiredLevel(questData.getRequiredLevel())
                .rewardXp(questData.getRewardXp())
                .rewardGold(questData.getRewardGold())
                .questType(questData.getQuestType())
                .build();

        questRepository.save(quest);
    }

    public void editQuest(EditQuestDTO questData) {
        Quest quest = questRepository.findById(questData.getId())
                .orElseThrow(() -> new QuestNotFoundException("Quest not found"));

        Optional<Quest> existingQuest = questRepository.findByTitle(questData.getTitle());

        if (existingQuest.isPresent() && !existingQuest.get().getId().equals(questData.getId())) {
            throw new QuestAlreadyExistsException("A quest with this name already exists"
            );
        }

        quest.setTitle(questData.getTitle());
        quest.setDescription(questData.getDescription());
        quest.setRequiredLevel(questData.getRequiredLevel());
        quest.setRewardXp(questData.getRewardXp());
        quest.setRewardGold(questData.getRewardGold());
        quest.setQuestType(questData.getQuestType());

        questRepository.save(quest);
    }


    public void deleteQuest(UUID questId) {
        if (!questRepository.existsById(questId)) {
            throw new QuestNotFoundException("Quest not found.");
        }

        questRepository.deleteById(questId);
    }
}
