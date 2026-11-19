package com.projet.auto_battler.service.dto;

import com.projet.auto_battler.entity.Dungeon;
import com.projet.auto_battler.entity.Player;
import com.projet.auto_battler.entity.PlayerProfile;

public record PlayerResponse(
        Long id,
        String username,
        String email,
        boolean admin,
        int totalVictories,
        int totalDefeats,
        int gold,
        Long highestDungeonId,
        String highestDungeonName) {

    public static PlayerResponse from(Player player) {
        PlayerProfile profile = player.getProfile();
        Dungeon dungeon = profile == null ? null : profile.getHighestDungeonReached();
        return new PlayerResponse(
                player.getId(),
                player.getUsername(),
                player.getEmail(),
                player.isAdmin(),
                profile == null ? 0 : profile.getTotalVictories(),
                profile == null ? 0 : profile.getTotalDefeats(),
                profile == null ? 0 : profile.getGold(),
                dungeon == null ? null : dungeon.getId(),
                dungeon == null ? null : dungeon.getName());
    }
    public double getWinrate() {
        int totalGames = totalVictories + totalDefeats;

        if (totalGames == 0) {
            return 0.0;
        }

        return (double) totalVictories / totalGames * 100;
    }
}
