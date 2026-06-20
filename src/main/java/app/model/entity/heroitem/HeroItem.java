package app.model.entity.heroitem;

import app.model.entity.hero.Hero;
import app.model.entity.item.Item;
import jakarta.persistence.*;

import lombok.*;

import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class HeroItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Hero hero;

    @ManyToOne
    private Item item;
}
