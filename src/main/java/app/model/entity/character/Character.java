package app.model.entity.character;

import app.model.entity.item.Item;
import app.model.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "characters")
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String roleplayName;

    @Enumerated(EnumType.STRING)
    private HeroClass heroClass;

    private int level;

    private int xp;

    private int gold;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "owner")
    private List<Item> items = new ArrayList<>();
}
