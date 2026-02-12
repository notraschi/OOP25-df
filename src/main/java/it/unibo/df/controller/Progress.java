package it.unibo.df.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import it.unibo.df.model.abilities.Ability;
import it.unibo.df.model.abilities.AbilityAreas;
import it.unibo.df.model.abilities.AbilityFn;

/**
 * Loads abilities from a YAML file.
 */
public final class Progress {
    private static final List<Integer> DEFAULT_UNLOCKED_IDS = List.of(1, 2, 3, 4, 5);
    private Map<Integer, Ability> unlockedAbilitiesById = new LinkedHashMap<>();
    private Map<Integer, Ability> lockedAbilitiesById = new LinkedHashMap<>();

    /**
     * Creates a registry loading abilities.yml from resources.
     */
    public Progress() {
        loadCurrent();
    }

    /**
     * updates progress.
     * 
     * @param killedEnemies amount of enemies killed in battle, unlocks one new
     *                      ability for each.
     */
    public void update(long killedEnemies) {
        final Random random = new Random();
        for (int i = 0; i < killedEnemies; i++) {
            List<Integer> keys = List.copyOf(lockedAbilitiesById.keySet());
            int index = random.nextInt(0, keys.size()); // % keys.size();
            unlockedAbilitiesById.put(keys.get(index), lockedAbilitiesById.get(keys.get(index)));
            lockedAbilitiesById.remove(keys.get(index));
        }
    }

    /**
     * resets progress to default.
     */
    public void reset() {
        var allAbilities = new LinkedHashMap<>(unlockedAbilitiesById);
        allAbilities.putAll(lockedAbilitiesById);

        unlockedAbilitiesById = allAbilities.values().stream()
            .filter(ab -> DEFAULT_UNLOCKED_IDS.contains(ab.id()))
            .collect(Collectors.toMap(ab -> ab.id(), ab -> ab));
        lockedAbilitiesById = allAbilities.values().stream()
            .filter(ab -> !DEFAULT_UNLOCKED_IDS.contains(ab.id()))
            .collect(Collectors.toMap(ab -> ab.id(), ab -> ab));
    }

    /**
     * writes progress to file.
     */
    @SuppressWarnings("unchecked")
    public void write() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("abilities.yml");
                FileWriter output = new FileWriter("src/main/resources/abilities.yml")) {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            Yaml file = new Yaml(options);
            Map<String, Object> root = file.load(input);
            List<Map<String, Object>> abilities = (List<Map<String, Object>>) root.get("abilities");
            for (Map<String, Object> ability : abilities) {
                if (unlockedAbilitiesById.keySet().contains((int) ability.get("id"))) {
                    ability.put("unlocked", true);
                } else {
                    ability.put("unlocked", false);
                }
            }
            file.dump(root, output);
        } catch (IOException e) {
            throw new IllegalStateException("Missing abilities list");
        }
    }

    /**
     * gets all unlocked abilities.
     * 
     * @return all unlocked abilities
     */
    public Map<Integer, Ability> unlockedAbilities() {
        return Map.copyOf(unlockedAbilitiesById);
    }

    /**
     * loads file as current progress.
     */
    private void loadCurrent() {
        loadGeneric().forEach(
                p -> (p.unlocked ? unlockedAbilitiesById : lockedAbilitiesById).put(p.ability.id(), p.ability));
    }

    /**
     * helper to load a generic progress
     * 
     * @return a stream of pairs containig an ability and a boolean to signify whether it's unlocked
     */
    private static Stream<Pair> loadGeneric() {
        // needs a "leading leash" ('/') in front of the file path.
        InputStream stream = Progress.class.getResourceAsStream("/abilities.yml");
        if (stream == null) {
            throw new IllegalStateException("abilities.yml not found");
        }

        return loadFromStream(stream).onClose(() -> {
            try {
                stream.close();
            } catch (IOException ex) {
                throw new AbilityLoadingException("could not close stream", ex);
            }
        });
    }

    /**
     * helper to load a generic progress from a input stream
     * 
     * @param stream the input stream
     * @return similar to {@link loadGeneric}
     */
    private static Stream<Pair> loadFromStream(final InputStream stream) {
        final Object loaded = new Yaml().load(stream);
        if (!(loaded instanceof Map<?, ?> root)) {
            throw new IllegalStateException("Invalid abilities.yml format");
        }

        final Object listObj = root.get("abilities");
        if (!(listObj instanceof List<?> list)) {
            throw new IllegalStateException("Missing abilities list");
        }

        return list.stream().map(entry -> getAbility(entry));
    }

    /**
     * reads a single ability from the file.
     * 
     * @param entry the entry to read
     * @return a pair of the ability read a boolean to signify whether it's unlocked
     */
    @SuppressWarnings("unchecked")
    private static Pair getAbility(final Object entry) {
        if (!(entry instanceof Map<?, ?>)) {
            throw new IllegalStateException("Invalid ability entry: " + entry);
        }

        final Map<String, Object> abilityData = (Map<String, Object>) entry;

        final int id = (int) abilityData.get("id");
        final String name = String.valueOf(abilityData.get("name"));
        final boolean unlocked = (boolean) abilityData.get("unlocked");
        final int cooldown = (int) abilityData.get("cooldown");
        final int casterHpDelta = (int) abilityData.get("casterHpDelta");
        final int targetHpDelta = (int) abilityData.get("targetHpDelta");

        final String area = String.valueOf(abilityData.getOrDefault("area", "NONE"))
                .toUpperCase(Locale.ROOT);

        final AbilityFn effect = AbilityAreas.fromString(area);

        return new Pair(
                unlocked,
                new Ability(
                        id,
                        name,
                        cooldown,
                        casterHpDelta,
                        targetHpDelta,
                        effect));
    }

    /**
     * gets all abilities in the file.
     * 
     * @return a map id-ability
     */
    public static Map<Integer, Ability> allRegisteredAbilities() {
        return loadGeneric().collect(Collectors.toMap(
                p -> p.ability.id(),
                p -> p.ability));
    }

    /**
     * simple pair to hold information about the ability, easier to move around.
     */
    private static record Pair(boolean unlocked, Ability ability) {
    }

    /**
     * custo exception to be more explicit during error handling.
     */
    public static class AbilityLoadingException extends UncheckedIOException {

        public AbilityLoadingException(String message, IOException cause) {
            super(message, cause);
        }
    }
}
