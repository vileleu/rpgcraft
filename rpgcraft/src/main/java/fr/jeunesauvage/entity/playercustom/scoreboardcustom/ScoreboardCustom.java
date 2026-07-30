package fr.jeunesauvage.entity.playercustom.scoreboardcustom;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Translatable;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Health;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Level;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Resource;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.ResourceManager;
import fr.jeunesauvage.entity.playercustom.classcustom.ClassCustom;
import fr.jeunesauvage.entity.playercustom.classcustom.ClassType;
import fr.jeunesauvage.entity.race.RaceCustom;
import fr.jeunesauvage.entity.race.RaceType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ScoreboardCustom {
    private final ResourceManager   resourceManager;
    private final RaceCustom        raceCustom;
    private final ClassCustom       classCustom;
    private final Scoreboard        scoreboard;
    private final Objective         objective;
    private final Team              team;

    public ScoreboardCustom(Player player, ResourceManager resourceManager, RaceCustom raceCustom, ClassCustom classCustom) {
        this.resourceManager = resourceManager;
        this.raceCustom = raceCustom;
        this.classCustom = classCustom;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.team = scoreboard.registerNewTeam("level");
        this.objective = scoreboard.registerNewObjective(
            "scoreboard",
            Criteria.DUMMY,
            Component.text("⚔ RpgCraft ⚔").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD)
        );
        team.addPlayer(player);
        for (ScoreboardType type: ScoreboardType.values()) {
            Score   score = objective.getScore(type.getName());
            score.setScore(type.getLine());
            write(score, type);
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }

    public void updateLevel(Level level) {
        Component   component = Component.text("[").color(NamedTextColor.GOLD)
            .append(Component.translatable("level.rpgcraft.short")
            .append(Component.text("] " + level.getValue())));
        team.prefix(component);
    }

    private void write(Score score, ScoreboardType type) {
        switch (type) {
            case ScoreboardType.VOID_START -> {
                score.customName(Component.text("---------------").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            }
            case ScoreboardType.VOID_END -> {
                score.customName(Component.text("---------------").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            }
            case ScoreboardType.HEALTH -> {
                Health    health = resourceManager.getHealth();
                Component   component = health.toComponent()
                    .append(Component.text(": "))
                    .append(Component.text((int)health.getValue()).color(health.getColor()))
                    .append(Component.text("/"))
                    .append(Component.text((int)health.getValueMax()).color(health.getColor()));
                score.customName(component);
            }
            case ScoreboardType.POWER -> {
                Resource    power = resourceManager.getPower();
                if (power == null)
                    score.customName(Component.text("--: --/--").decorate(TextDecoration.BOLD));
                else {
                    Component   component = power.toComponent()
                        .append(Component.text(": "))
                        .append(Component.text((int)power.getValue()).color(power.getColor()))
                        .append(Component.text("/"))
                        .append(Component.text((int)power.getValueMax()).color(power.getColor()));
                    score.customName(component);
                }
            }
            case ScoreboardType.CLASS -> {
                ClassType   classType = classCustom.getClassType();
                Component   component = Translatable.classType()
                    .append(Component.text(": "))
                    .append(classType.toComponent().color(classType.getColor()));
                score.customName(component);
            }
            case ScoreboardType.RACE -> {
                RaceType    raceType = raceCustom.getRaceType();
                Component   component = Translatable.raceType()
                    .append(Component.text(": "))
                    .append(raceType.toComponent().color(raceType.getColor()));
                score.customName(component);
            }
        };
    }

    // refresh

    public void refreshAll() {
        refreshHealth();
        refreshPower();
        refreshClass();
        refreshRace();
    }

    public void refreshHealth() {
        Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
            ScoreboardType  type = ScoreboardType.HEALTH;
            Score   score = objective.getScore(type.getName());
            write(score, type);
        });
    }

    public void refreshPower() {
        Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
            ScoreboardType  type = ScoreboardType.POWER;
            Score   score = objective.getScore(type.getName());
            write(score, type);
        });
    }

    public void refreshClass() {
        Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
            ScoreboardType  type = ScoreboardType.CLASS;
            Score   score = objective.getScore(type.getName());
            write(score, type);
        });
    }

    public void refreshRace() {
        Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
            ScoreboardType  type = ScoreboardType.RACE;
            Score   score = objective.getScore(type.getName());
            write(score, type);
        });
    }
}
