package scrabble.rules.game;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class GameRules {
    private GameRules() {
    }

    public static void load(String jsonFilePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(jsonFilePath));

            // =======================
            // --- Load BagConstants ---
            // =======================
            JsonNode bagNode = root.get("bag");

            // Deserialize TILE_DATA as Map<String, BagData.TileData>
            Map<String, BagConstants.BagData.TileData> rawTiles = mapper.convertValue(
                    bagNode.get("TILE_DATA"),
                    new TypeReference<Map<String, BagConstants.BagData.TileData>>() {
                    });

            // Convert to Map<Character, LetterData>
            Map<Character, BagConstants.LetterData> tileMap = new HashMap<>();
            for (Map.Entry<String, BagConstants.BagData.TileData> entry : rawTiles.entrySet()) {
                char letter = entry.getKey().charAt(0);
                BagConstants.BagData.TileData td = entry.getValue();
                tileMap.put(letter, new BagConstants.LetterData(td.count, td.score));
            }

            // Build BagData instance
            BagConstants.BagData bagData = new BagConstants.BagData();
            bagData.TILE_DATA = tileMap;
            bagData.BLANK = bagNode.get("BLANK").asText().charAt(0);

            // Initialize BagConstants
            BagConstants.initialize(bagData);

            // =========================
            // --- Load BoardConstants ---
            // =========================
            BoardConstants.BoardData boardData = mapper.treeToValue(root.get("board"), BoardConstants.BoardData.class);

            // Convert int[] to byte[]
            byte[] boardArray = new byte[boardData.BOARD_BONUSES.length];
            for (int i = 0; i < boardData.BOARD_BONUSES.length; i++) {
                boardArray[i] = (byte) boardData.BOARD_BONUSES[i];
            }

            // Prepare BoardData for initialization
            BoardConstants.BoardData boardInit = new BoardConstants.BoardData();
            boardInit.SIZE = boardData.SIZE;
            boardInit.TOTAL_SIZE = boardData.TOTAL_SIZE;
            boardInit.BOARD_BONUSES = boardArray;

            BoardConstants.initialize(boardInit);

            // =========================
            // --- Load GameConstants ---
            // =========================
            GameConstants.GameData gameData = mapper.treeToValue(root.get("game"), GameConstants.GameData.class);

            GameConstants.GameData gameInit = new GameConstants.GameData();
            gameInit.EMPTY_SQUARE = gameData.EMPTY_SQUARE; // single-character string → char
            gameInit.BINGO_BONUS = gameData.BINGO_BONUS;
            gameInit.RACK_SIZE = gameData.RACK_SIZE;

            GameConstants.initialize(gameInit);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load game rules from JSON", e);
        }
    }
}