package app.service.quest;

import app.exception.HeroNotFoundException;
import app.exception.QuestAlreadyExistsException;
import app.exception.QuestNotFoundException;
import app.mapper.quest.QuestMapper;
import app.model.dto.event.ActiveEventResponse;
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
import app.service.event.EventService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class QuestService {
    private final QuestRepository questRepository;
    private final HeroRepository heroRepository;
    private final EventService eventService;

    @Autowired
    public QuestService(QuestRepository questRepository, HeroRepository heroRepository, EventService eventService) {
        this.questRepository = questRepository;
        this.heroRepository = heroRepository;
        this.eventService = eventService;
    }

    public List<QuestDTO> getAllQuests() {
        log.debug("Fetching all quests");

        List<QuestDTO> quests =  questRepository.findAll().stream().map(QuestMapper::toQuestDTO).toList();

        log.debug("Fetched {} quests", quests.size());

        return quests;
    }

    public QuestResultDTO completeQuest(UUID id, UUID userId) {
        Hero hero = heroRepository.findByUserId(userId)
                .orElseThrow(HeroNotFoundException::new);

        Quest quest = questRepository.findById(id)
                .orElseThrow(QuestNotFoundException::new);

        if (hero.getHeroClass() == HeroClass.WARRIOR && quest.getQuestType() != QuestType.COMBAT
                || hero.getHeroClass() == HeroClass.MAGE && quest.getQuestType() != QuestType.MAGIC
                || hero.getHeroClass() == HeroClass.ROGUE && quest.getQuestType() != QuestType.STEALTH
                || hero.getHeroClass() == HeroClass.HEALER && quest.getQuestType() != QuestType.SUPPORT) {

            log.debug("User {} cannot complete quest '{}' because hero class {} does not match quest type {}",
                    userId, quest.getTitle(), hero.getHeroClass(), quest.getQuestType());

            return QuestResultDTO.builder()
                    .success(false)
                    .message("Your hero class cannot complete this quest.")
                    .build();
        }

        if (hero.getLevel() < quest.getRequiredLevel()) {
            log.debug("User {} cannot complete quest '{}' because hero level {} is below required level {}",
                    userId, quest.getTitle(), hero.getLevel(), quest.getRequiredLevel());

            return QuestResultDTO.builder()
                    .success(false)
                    .message("Your level is too low for this quest.")
                    .build();
        }

        hero.setXp(hero.getXp() + quest.getRewardXp());
        hero.setLevel(hero.getXp() / 100 + 1);
        hero.setGold(hero.getGold() + quest.getRewardGold());

        QuestResultDTO result = QuestResultDTO.builder()
                .success(true)
                .message("You earned " + quest.getRewardXp() + " XP and " + quest.getRewardGold() + " gold!\n").build();

        log.info("User {} completed quest {}", userId, quest.getTitle());

        ActiveEventResponse event = eventService.getActiveEvent();

        if(event != null && event.getAffectedQuestType() == quest.getQuestType()){

            hero.setXp(hero.getXp() + event.getBonusXp());
            hero.setGold(hero.getGold() + event.getBonusGold());

            result.setMessage(result.getMessage() + String.format(
                            "Kingdom Event: %s\n" +
                            "Bonus: +%d XP and +%d Gold\n" +
                            "Total: %d XP and %d Gold\n",
                            event.getTitle(), event.getBonusXp(), event.getBonusGold(),
                            quest.getRewardXp() + event.getBonusXp(),
                            quest.getRewardGold() + event.getBonusGold()));

            log.info("Kingdom event '{}' applied bonus rewards to user {} for quest '{}'",
                    event.getTitle(), userId, quest.getTitle());
        }

        heroRepository.save(hero);

        log.info("Rewards saved for user {} after completing quest '{}'", userId, quest.getTitle());

        return result;
    }

    public void createQuest(CreateQuestDTO questData) {
        log.info("Creating quest with title '{}'", questData.getTitle());

        if (questRepository.existsByTitle(questData.getTitle())) {
            throw new QuestAlreadyExistsException(questData.getTitle());
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

        log.info("Quest '{}' created successfully with ID {}", quest.getTitle(), quest.getId());
    }

    public void editQuest(EditQuestDTO questData) {
        log.info("Editing quest with ID {}", questData.getId());

        Quest quest = questRepository.findById(questData.getId())
                .orElseThrow(QuestNotFoundException::new);

        Optional<Quest> existingQuest = questRepository.findByTitle(questData.getTitle());

        if (existingQuest.isPresent() && !existingQuest.get().getId().equals(questData.getId())) {
            throw new QuestAlreadyExistsException(questData.getTitle());
        }

        quest.setTitle(questData.getTitle());
        quest.setDescription(questData.getDescription());
        quest.setRequiredLevel(questData.getRequiredLevel());
        quest.setRewardXp(questData.getRewardXp());
        quest.setRewardGold(questData.getRewardGold());
        quest.setQuestType(questData.getQuestType());

        questRepository.save(quest);

        log.info("Quest with ID {} edited successfully", quest.getId());
    }

    public void deleteQuest(UUID questId) {
        log.info("Deleting quest with ID {}", questId);

        if (!questRepository.existsById(questId)) {
            throw new QuestNotFoundException();
        }

        questRepository.deleteById(questId);

        log.info("Quest with ID {} deleted successfully", questId);
    }
}
