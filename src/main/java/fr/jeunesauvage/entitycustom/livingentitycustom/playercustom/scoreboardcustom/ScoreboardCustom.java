package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.scoreboardcustom;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import net.kyori.adventure.text.format.NamedTextColor;

public class ScoreboardCustom {
    private final Scoreboard        scoreboard;
    private final Objective         objective;

    public ScoreboardCustom(PlayerCustom playerCustom) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective(
            "scoreboard",
            Criteria.DUMMY,
            Message.c("⚔ RpgCraft ⚔", NamedTextColor.GOLD)
        );
        for (ScoreboardType type: ScoreboardType.values()) {
            Score   score = objective.getScore(type.getName());
            score.setScore(type.getLine());
            write(playerCustom, score, type);
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        playerCustom.getPlayer().setScoreboard(scoreboard);
    }

    private void write(PlayerCustom playerCustom, Score score, ScoreboardType type) {
        switch (type) {
            case VOID_START, VOID_END -> score.customName(Message.c("---------------", NamedTextColor.GOLD));
            case HEALTH -> score.customName(Message.health(playerCustom.getHealth(), playerCustom.getHealthMax()));
            case POWER ->  score.customName(Message.power(playerCustom.getPowerCustom()));
            case RACE -> score.customName(Message.raceType(playerCustom.getRaceType()));
            case CLASS -> score.customName(Message.classType(playerCustom.getClassType()));
            case ALLY -> score.customName(Message.ally(playerCustom.getAlly()));
            case ALLY_LOCATION -> score.customName(Message.allyLocation(playerCustom.getAlly()));
        };
    }

    // refresh

    public void refreshAll(PlayerCustom playerCustom) {
        refreshHealth(playerCustom);
        refreshPower(playerCustom);
        refreshRace(playerCustom);
        refreshClass(playerCustom);
        refreshAlly(playerCustom);
    }

    public void refreshHealth(PlayerCustom playerCustom) {
        Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
            ScoreboardType  type = ScoreboardType.HEALTH;
            Score   score = objective.getScore(type.getName());
            write(playerCustom, score, type);
        });
    }

    public void refreshPower(PlayerCustom playerCustom) {
        Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
            ScoreboardType  type = ScoreboardType.POWER;
            Score   score = objective.getScore(type.getName());
            write(playerCustom, score, type);
        });
    }

    public void refreshRace(PlayerCustom playerCustom) {
        Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
            ScoreboardType  type = ScoreboardType.RACE;
            Score   score = objective.getScore(type.getName());
            write(playerCustom, score, type);
        });
    }

    public void refreshClass(PlayerCustom playerCustom) {
        Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
            ScoreboardType  type = ScoreboardType.CLASS;
            Score   score = objective.getScore(type.getName());
            write(playerCustom, score, type);
        });
    }

    public void refreshAlly(PlayerCustom playerCustom) {
        Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
            ScoreboardType  type = ScoreboardType.ALLY;
            Score   score = objective.getScore(type.getName());
            write(playerCustom, score, type);
            type = ScoreboardType.ALLY_LOCATION;
            score = objective.getScore(type.getName());
            write(playerCustom, score, type);
        });
    }
}
