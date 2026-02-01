package it.unibo.df.model.abilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

/**
 * Loads abilities from a YAML file.
 */
public final class AbilityRegistry {

    private final Map<Integer, Ability> abilitiesById = new HashMap<>();

    /**
     * Creates a registry loading abilities.yml from resources.
     */
    public AbilityRegistry() {
        loadDefault();
    }

    /**
     * Returns an ability by id.
     *
     * @param id ability id
     * @return ability or null
     */
    public Ability get(final int id) {
        return abilitiesById.get(id);
    }

    /**
     * Returns all registered abilities.
     *
     * @return list of abilities
     */
    public List<Ability> getAll() {
        return List.copyOf(abilitiesById.values());
    }

    /**
     * Returns how many abilities are registered.
     *
     * @return number of abilities
     */
    public int size() {
        return abilitiesById.size();
    }

    private void loadDefault() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("abilities.yml")) {
            if (stream == null) {
                throw new IllegalStateException("abilities.yml not found");
            }
            loadFromStream(stream);
        } catch (final IOException e) {
            throw new IllegalStateException("Cannot read abilities.yml", e);
        }
    }

    private void loadFromStream(final InputStream stream) {
        final Object loaded = new Yaml().load(stream);
        if (!(loaded instanceof Map<?, ?> root)) {
            throw new IllegalStateException("Invalid abilities.yml format");
        }

        final Object listObj = root.get("abilities");
        if (!(listObj instanceof List<?> list)) {
            throw new IllegalStateException("Missing abilities list");
        }

        for (final Object entry : list) {
            addAbility(entry);
        }
    }

    @SuppressWarnings("unchecked")
    private void addAbility(final Object entry) {
        if (!(entry instanceof Map<?, ?>)) {
            throw new IllegalStateException("Invalid ability entry: " + entry);
        }

        final Map<String, Object> abilityData = (Map<String, Object>) entry;

        final int id = (int) abilityData.get("id");
        final String name = String.valueOf(abilityData.get("name"));
        final int cooldown = (int) abilityData.get("cooldown");
        final AbilityType type = AbilityType.valueOf(
            String.valueOf(abilityData.get("type")).toUpperCase(Locale.ROOT)
        );
        final int casterHpDelta = (int) abilityData.get("casterHpDelta");
        final int targetHpDelta = (int) abilityData.get("targetHpDelta");

        final String area = String.valueOf(abilityData.getOrDefault("area", "NONE"))
            .toUpperCase(Locale.ROOT);

        final AbilityFn effect = switch (area) {
            case "NONE" -> caster -> Optional.empty();
            case "SELF" -> caster -> Optional.of(Set.of(caster));
            case "ADJ4" -> AbilityAreas.adjacent4();
            default -> throw new IllegalArgumentException("Unknown area: " + area);
        };


        abilitiesById.put(id,new Ability(
            id,
            name,
            cooldown,
            type,
            casterHpDelta,
            targetHpDelta,
            effect
        ));
    }
}
