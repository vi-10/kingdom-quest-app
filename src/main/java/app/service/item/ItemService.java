package app.service.item;

import app.mapper.item.ItemMapper;
import app.mapper.quest.QuestMapper;
import app.model.dto.item.ItemDTO;
import app.model.dto.quest.QuestDTO;
import app.repository.item.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ItemService {
    private ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll().stream().map(ItemMapper::toItemDTO).toList();
    }
}
