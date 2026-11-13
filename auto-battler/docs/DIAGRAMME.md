# Diagrammes du projet

Référence de l'équipe pour le modèle de données et l'architecture du moteur de combat.

## 1. MCD (modèle entité-association)

Les relations N-N sont dessinées directement, sans les tables de jointure
(`team_hero`, `hero_skill`, `hero_equipment`, `dungeon_monster`), générées par JPA.

```mermaid
erDiagram
    PLAYER ||--|| PLAYER_PROFILE : "possède"
    PLAYER ||--o{ TEAM : "crée"
    TEAM }o--o{ HERO : "est composée de"
    HERO }o--o{ SKILL : "maîtrise"
    HERO }o--o{ EQUIPMENT : "porte"
    DUNGEON }o--o{ MONSTER : "contient"
    TEAM ||--o{ BATTLE : "participe à"
    DUNGEON ||--o{ BATTLE : "accueille"
    BATTLE ||--o{ BATTLE_LOG_ENTRY : "journalise"

    PLAYER {
        long id PK
        string username UK
        string email
        datetime created_at
        long profile_id FK
    }
    PLAYER_PROFILE {
        long id PK
        int total_victories
        int total_defeats
        int gold
        int highest_dungeon_reached
    }
    HERO {
        long id PK
        string name UK
        string description
        string role "enum"
        int health
        int attack_damage
        int defense
        int speed
        int level
        int experience
        boolean unlockable
        boolean unlocked
    }
    SKILL {
        long id PK
        string name UK
        string description
        string skill_type "enum"
        int power
        int cooldown
        int duration
    }
    EQUIPMENT {
        long id PK
        string name UK
        string slot "enum"
        string rarity "enum"
        int bonus_attack
        int bonus_health
        int bonus_defense
        int bonus_speed
    }
    MONSTER {
        long id PK
        string name UK
        string description
        string monster_type "enum"
        int health
        int damage
        int defense
        int speed
        boolean boss
    }
    DUNGEON {
        long id PK
        string name UK
        string description
        int difficulty
        string unlock_condition
        boolean unlocked
    }
    TEAM {
        long id PK
        string name
        long player_id FK
    }
    BATTLE {
        long id PK
        datetime started_at
        datetime ended_at
        string status "enum"
        long seed
        int turns_played
        int xp_gained
        int gold_gained
        long team_id FK
        long dungeon_id FK
    }
    BATTLE_LOG_ENTRY {
        long id PK
        int turn
        string actor_name
        string entry_type "enum"
        string message
        long battle_id FK
    }
```

## 2. Diagramme de classes : entités

Côté propriétaire des relations N-N (`@JoinTable`) : `Team` (heroes), `Hero` (skills, equipment), `Dungeon` (monsters).

```mermaid
classDiagram
    direction LR

    class Player {
        -Long id
        -String username
        -String email
        -LocalDateTime createdAt
        -PlayerProfile profile
        -List~Team~ teams
    }
    class PlayerProfile {
        -Long id
        -int totalVictories
        -int totalDefeats
        -int gold
        -int highestDungeonReached
    }
    class Team {
        -Long id
        -String name
        -Player player
        -Set~Hero~ heroes
        -List~Battle~ battles
    }
    class Hero {
        -Long id
        -String name
        -String description
        -HeroRole role
        -int health
        -int attackDamage
        -int defense
        -int speed
        -int level
        -int experience
        -boolean unlockable
        -boolean unlocked
        -Set~Skill~ skills
        -Set~Equipment~ equipment
    }
    class Skill {
        -Long id
        -String name
        -String description
        -SkillType skillType
        -int power
        -int cooldown
        -int duration
    }
    class Equipment {
        -Long id
        -String name
        -EquipmentSlot slot
        -Rarity rarity
        -int bonusAttack
        -int bonusHealth
        -int bonusDefense
        -int bonusSpeed
    }
    class Monster {
        -Long id
        -String name
        -String description
        -MonsterType monsterType
        -int health
        -int damage
        -int defense
        -int speed
        -boolean boss
    }
    class Dungeon {
        -Long id
        -String name
        -String description
        -int difficulty
        -String unlockCondition
        -boolean unlocked
        -Set~Monster~ monsters
        -List~Battle~ battles
    }
    class Battle {
        -Long id
        -LocalDateTime startedAt
        -LocalDateTime endedAt
        -BattleStatus status
        -long seed
        -int turnsPlayed
        -int xpGained
        -int goldGained
        -Team team
        -Dungeon dungeon
        -List~BattleLogEntry~ log
    }
    class BattleLogEntry {
        -Long id
        -int turn
        -String actorName
        -LogEntryType entryType
        -String message
    }

    class HeroRole {
        <<enumeration>>
        WARRIOR
        MAGE
        ARCHER
        CLERIC
    }
    class SkillType {
        <<enumeration>>
        ATTACK
        HEAL
        SHIELD
        POISON
    }
    class EquipmentSlot {
        <<enumeration>>
        WEAPON
        ARMOR
        ACCESSORY
    }
    class Rarity {
        <<enumeration>>
        COMMON
        RARE
        EPIC
        LEGENDARY
    }
    class MonsterType {
        <<enumeration>>
        BEAST
        UNDEAD
        DEMON
        GOBLIN
    }
    class BattleStatus {
        <<enumeration>>
        IN_PROGRESS
        VICTORY
        DEFEAT
        ABANDONED
    }
    class LogEntryType {
        <<enumeration>>
        ATTACK
        SKILL
        HEAL
        POISON
        SHIELD
        DEATH
        END
    }

    Player "1" *-- "1" PlayerProfile : profil avec cascade
    Player "1" -- "0..*" Team : equipes
    Team "0..*" -- "0..*" Hero : composee de 1 a 4 heros
    Hero "0..*" -- "0..*" Skill : competences
    Hero "0..*" -- "0..*" Equipment : equipements
    Dungeon "0..*" -- "0..*" Monster : monstres dont 1 boss max
    Team "1" -- "0..*" Battle : combats
    Dungeon "1" -- "0..*" Battle : combats
    Battle "1" *-- "0..*" BattleLogEntry : journal avec cascade et orphanRemoval

    Hero ..> HeroRole
    Skill ..> SkillType
    Equipment ..> EquipmentSlot
    Equipment ..> Rarity
    Monster ..> MonsterType
    Battle ..> BattleStatus
    BattleLogEntry ..> LogEntryType
```

## 3. Diagramme de classes : services et moteur de combat

Le moteur (`combat/`) est du Java pur, sans Spring ni JPA.

```mermaid
classDiagram
    direction LR

    class BattleService {
        +launch(teamId, dungeonId) BattleResponse
        +getBattle(id) BattleResponse
        +getLog(id) List~LogEntryResponse~
    }
    class ProgressionService {
        +applyRewards(battle)
        +checkUnlocks(player)
        +getProgression(playerId) ProgressionResponse
    }
    class TeamService {
        +addHero(teamId, heroId)
        +removeHero(teamId, heroId)
    }
    class DungeonService {
        +addMonster(dungeonId, monsterId)
        +threatScore(dungeonId) int
    }
    class CombatEngine {
        +simulate(heroes, monsters, seed) CombatResult
    }
    class Combatant {
        -String name
        -int maxHealth
        -int health
        -int attack
        -int defense
        -int speed
        -boolean hero
        -List~SkillSnapshot~ skills
        -List~ActiveEffect~ effects
        +isAlive() boolean
    }
    class SkillSnapshot {
        -SkillType type
        -int power
        -int cooldown
        -int duration
        -int cooldownRemaining
    }
    class ActiveEffect {
        -EffectType type
        -int value
        -int remainingTurns
    }
    class CombatResult {
        -boolean heroesWon
        -int turns
        -List~CombatEvent~ events
    }
    class CombatEvent {
        -int turn
        -String actorName
        -String type
        -String message
    }
    class EffectType {
        <<enumeration>>
        POISON
        SHIELD
    }

    BattleService --> CombatEngine : appelle
    BattleService --> ProgressionService : recompenses
    BattleService ..> Combatant : construit un snapshot
    CombatEngine ..> Combatant
    CombatEngine --> CombatResult : renvoie
    Combatant "1" *-- "0..*" SkillSnapshot
    Combatant "1" *-- "0..*" ActiveEffect
    CombatResult "1" *-- "0..*" CombatEvent
    ActiveEffect ..> EffectType
```
