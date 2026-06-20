package app.model.entity.item;

import app.model.entity.hero.Hero;
import app.model.entity.hero.HeroClass;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Enumerated(EnumType.STRING)
    private HeroClass heroClass;

    private int requiredGold;

    @Enumerated(EnumType.STRING)
    private ItemRarity rarity;

}
