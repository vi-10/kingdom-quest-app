package app.service.heroitem;

import app.repository.heroitem.HeroItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class HeroItemService {
    private HeroItemRepository heroItemRepository;

    @Autowired
    public HeroItemService(HeroItemRepository heroItemRepository) {
        this.heroItemRepository = heroItemRepository;
    }
}
