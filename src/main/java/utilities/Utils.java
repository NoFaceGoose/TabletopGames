package utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public abstract class Utils {

    public static Color stringToColor(String c) {
        switch (c.toLowerCase()) {
            case "blue":
                return Color.BLUE;
            case "black":
                return Color.BLACK;
            case "yellow":
                return Color.YELLOW;
            case "red":
                return Color.RED;
            case "green":
                return new Color(30, 108, 47);
            case "white":
                return Color.WHITE;
            case "brown":
                return new Color(69, 29, 26);
            case "pink":
                return Color.PINK;
            case "orange":
                return Color.ORANGE;
            case "light green":
                return Color.GREEN;
            default:
                return null;
        }
    }

    /**
     * Finds index in array of String objects.
     *
     * @param array  - array of String
     * @param object - object to look for
     * @return - index of String object, -1 if not found
     */
    public static int indexOf(String[] array, String object) {
        for (int i = 0; i < array.length; i++) {
            if (object.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds index of integer in array.
     *
     * @param array  - array of integers
     * @param object - integer to look for
     * @return - index of integer object, -1 if not found
     */
    public static int indexOf(int[] array, int object) {
        for (int i = 0; i < array.length; i++) {
            if (object == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Generates all permutations of a given array of integers.
     *
     * @param n        - current index to search up to
     * @param elements - array with elements
     * @param all      - list where all permutations should be added
     */
    public static void generatePermutations(int n, int[] elements, ArrayList<int[]> all) {
        if (n == 1) {
            all.add(elements.clone());
        } else {
            for (int i = 0; i < n - 1; i++) {
                generatePermutations(n - 1, elements, all);
                if (n % 2 == 0) {
                    swap(elements, i, n - 1);
                } else {
                    swap(elements, 0, n - 1);
                }
            }
            generatePermutations(n - 1, elements, all);
        }
    }

    /**
     * Performs a swap of 2 elements in an integer array at given indexes. Modifies original array.
     *
     * @param input - input array
     * @param a     - index of first element
     * @param b     - index of second element
     */
    public static void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }

    /**
     * Find a list of coordinates for neighbours of point at (x, y) in 2D grid of given width and height, with either
     * 8-way or 4-way connectivity.
     *
     * @param x      - x coordinate of point
     * @param y      - y coordinate of point
     * @param width  - width of grid
     * @param height - height of grid
     * @param way8   - if true, grid has 8-way connectivity, otherwise just 4.
     * @return List of Vector2D, coordinates for valid neighbours
     */
    public static java.util.List<Vector2D> getNeighbourhood(int x, int y, int width, int height, boolean way8) {
        List<Vector2D> neighbours = new ArrayList<>();

        // Add orthogonal neighbours
        if (x > 0) neighbours.add(new Vector2D(x - 1, y));
        if (x < width - 1) neighbours.add(new Vector2D(x + 1, y));
        if (y > 0) neighbours.add(new Vector2D(x, y - 1));
        if (y < height - 1) neighbours.add(new Vector2D(x, y + 1));

        // Add diagonal neighbours
        if (way8) {
            if (x > 0 && y > 0) neighbours.add(new Vector2D(x - 1, y - 1));
            if (x < width - 1 && y < height - 1) neighbours.add(new Vector2D(x + 1, y + 1));
            if (x > 0 && y < height - 1) neighbours.add(new Vector2D(x - 1, y + 1));
            if (x < width - 1 && y > 0) neighbours.add(new Vector2D(x + 1, y - 1));
        }
        return neighbours;
    }

    /**
     * Normalizes a value in range [0, 1] given its minimum and maximum possible.
     *
     * @param a_value - value to normalize
     * @param a_min   - minimum possible
     * @param a_max   - maximum possible
     * @return - normalized value
     */
    public static double normalise(double a_value, double a_min, double a_max) {
        if (a_min < a_max)
            return (a_value - a_min) / (a_max - a_min);
        else if (a_min == a_max)
            return 0.0; // special case
        throw new IllegalArgumentException(String.format("Invalid args in Utils.normalise() - %.3f is not in range [%.3f, %.3f]", a_value, a_min, a_max));
    }

    /**
     * Applies random noise to input.
     *
     * @param input   - value to apply noise to.
     * @param epsilon - how much should the noise weigh in returned value.
     * @param random  - how much noise should be applied.
     * @return - new value with noise applied.
     */
    public static double noise(double input, double epsilon, double random) {
        return (input + epsilon) * (1.0 + epsilon * (random - 0.5));
    }

    public static double entropyOf(double... data) {
        double sum = Arrays.stream(data).sum();
        double[] normalised = Arrays.stream(data).map(d -> d / sum).toArray();
        return Arrays.stream(normalised).map(d -> -d * Math.log(d)).sum();
    }

    public static <T> Map<T, Double> normaliseMap(Map<T, ? extends Number> input) {
        int lessThanZero = (int) input.values().stream().filter(n -> n.doubleValue() < 0.0).count();
        if (lessThanZero > 0) throw new AssertionError("Probability has negative values!");
        double sum = input.values().stream().mapToDouble(Number::doubleValue).sum();
        if (sum == 0.0) {
            // the sum is zero, with no negative values. Hence all values are zero, and we return a uniform distribution.
            return input.keySet().stream().collect(toMap(key -> key, key -> 1.0 / input.size()));
        }
        return input.keySet().stream().collect(toMap(key -> key, key -> input.get(key).doubleValue() / sum));
    }

    /**
     * This decays statistics by gamma
     *
     * @param pair
     * @param gamma
     * @return
     */
    public static Pair<Integer, Double> decay(Pair<Integer, Double> pair, double gamma) {
        if (gamma < 1.0 && gamma >= 0.0) {
            if (pair.a == 0) return new Pair<>(0, 0.0);
            double oldCount = pair.a;
            int newCount = (int) (oldCount * gamma);
            double newValue = pair.b * newCount / oldCount;
            return new Pair<>(newCount, newValue);
        }
        return pair;
    }

    public static <T> Map<T, Pair<Integer, Double>> decay(Map<T, Pair<Integer, Double>> map, double gamma) {
        return map.keySet().stream()
                .collect(toMap(key -> key, key -> decay(map.get(key), gamma)));
    }

    @SuppressWarnings("unchecked")
    public static <T> T getArg(String[] args, String name, T defaultValue) {
        Optional<String> raw = Arrays.stream(args).filter(i -> i.toLowerCase().startsWith(name.toLowerCase() + "=")).findFirst();
        if (raw.isPresent()) {
            String rawString = raw.get().split("=")[1];
            if (defaultValue instanceof Integer) {
                return (T) Integer.valueOf(rawString);
            } else if (defaultValue instanceof Double) {
                return (T) Double.valueOf(rawString);
            } else if (defaultValue instanceof Boolean) {
                return (T) Boolean.valueOf(rawString);
            } else if (defaultValue instanceof String) {
                return (T) rawString;
            } else {
                throw new AssertionError("Unexpected type of defaultValue : " + defaultValue.getClass());
            }
        }
        return defaultValue;
    }

    public static JSONObject loadJSONFile(String fileName) {
        try {
            FileReader reader = new FileReader(fileName);
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(reader);
        } catch (IOException | ParseException e) {
            throw new AssertionError("Error processing file " + fileName + " : " + e.getMessage() + " : " + e.toString());
        }
    }

    /**
     * Recursively computes combinations of numbers in an array, taken {r} at a time. Each combination is added into the
     * allData list.
     *
     * @param arr   - Input Array
     * @param data  - Temporary array to store current combination
     * @param start - Staring index in arr for current iteration
     * @param end   - Ending index in arr for current iteration
     * @param index - Current index in data
     * @param r     ---> Size of a combination
     */
    public static void combinationUtil(int[] arr, int[] data, int start, int end, int index, int r, ArrayList<int[]> allData) {
        if (index == r) {
            allData.add(data.clone());
            return;
        }

        for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
            data[index] = arr[i];
            combinationUtil(arr, data, i + 1, end, index + 1, r, allData);
        }
    }

    /**
     * Auxiliary function shortcut to generate combinations of numbers in an array, each of size r.
     *
     * @param arr - input array
     * @param r   - size of one combination
     * @return a list of all possible combinations of values (!not indexes)
     */
    public static ArrayList<int[]> generateCombinations(int[] arr, int r) {
        int[] data = new int[r];
        ArrayList<int[]> allData = new ArrayList<>();
        combinationUtil(arr, data, 0, arr.length - 1, 0, r, allData);
        return allData;
    }

    /**
     * Given a JSONObject, this will load the instance of the class.
     * this assumes that the JSON object has:
     * - a "class" attribute with the full name of the Class
     * - an (optional) "args" Array attribute with the values to feed into the class constructor
     * - only int, double, boolean and string parameters are allowed
     * - the relevant constructor of the class is then called, and the result returned
     */
    @SuppressWarnings("unchecked")
    public static <T> T loadClassFromJSON(JSONObject json) {
        try {
            String cl = (String) json.getOrDefault("class", "");
            Class<T> outputClass = (Class<T>) Class.forName(cl);
            JSONArray argArray = (JSONArray) json.getOrDefault("args", Collections.emptyList());
            Class<?>[] argClasses = new Class[argArray.size()];
            Object[] args = new Object[argArray.size()];
            for (int i = 0; i < argClasses.length; i++) {
                Object arg = argArray.get(i);
                args[i] = arg;
                if (arg instanceof Long) {
                    argClasses[i] = int.class;
                    args[i] = ((Long) arg).intValue();
                } else if (arg instanceof Double) {
                    argClasses[i] = double.class;
                } else if (arg instanceof Boolean) {
                    argClasses[i] = boolean.class;
                } else if (arg instanceof String) {
                    argClasses[i] = String.class;
                } else {
                    throw new AssertionError("Unexpected arg " + arg + " in " + json.toJSONString());
                }
            }
            Class<?> clazz = Class.forName(cl);
            Constructor<?> constructor = clazz.getConstructor(argClasses);
            return outputClass.cast(constructor.newInstance(args));
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Unknown class in " + json.toJSONString() + " : " + e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new AssertionError("No matching constructor for class found using " + json.toJSONString());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            throw new AssertionError("Error constructing class using " + json.toJSONString());
        }
    }

    /**
     * Given a filename that contains only a single class, this will instantiate the class
     * This opens the file, extracts the JSONObject, and then uses Utils.loadClassFromJSON() to
     * find and call the relevant constructor
     *
     * @param filename - the filename
     * @param <T>      - the Class type that is to be instantiated
     * @return
     */
    public static <T> T loadFromFile(String filename) {
        try {
            FileReader reader = new FileReader(filename);
            JSONParser jsonParser = new JSONParser();
            JSONObject rawData = (JSONObject) jsonParser.parse(reader);
            // We expect a class field to tell us the Class to use
            // then a set of parameter values
            return Utils.loadClassFromJSON(rawData);

        } catch (FileNotFoundException e) {
            throw new AssertionError("File not found to load IGameHeuristic : " + filename);
        } catch (IOException e) {
            throw new AssertionError("Problem reading file " + filename + " : " + e);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new AssertionError("Problem parsing JSON in " + filename);
        }
    }

    public enum ComponentType {
        DECK,
        AREA,
        BOARD,
        BOARD_NODE,
        CARD,
        COUNTER,
        DICE,
        TOKEN
    }

    public enum GameResult {
        WIN(1),
        DRAW(0),
        LOSE(-1),
        DISQUALIFY(-2),
        GAME_ONGOING(0),
        GAME_END(3);

        public final double value;

        GameResult(double value) {
            this.value = value;
        }
    }

}
