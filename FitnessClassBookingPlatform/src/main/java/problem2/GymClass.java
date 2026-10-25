package problem2;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * The type Gym class.
 * Each class has a unique ID, a name (e.g. "Yoga", "HIIT", "Spin"), an instructor, a studio
 * room, a difficulty level, a list of required equipment, a date/time, and a maximum capacity.
 */
public class GymClass {

    private int id; // BookingManager owns ID assignment
    private String name;
    private String instructor;
    private String studio;
    private String difficulty;
    private List<String> equipment;
    private String dateTime;
    private int capacity;
    private Set<String> bookedMembers = new HashSet<>();
    private double basePrice; // Holds base price + class info

    /**
     * Instantiates a new Gym class.
     *
     * @param name       the name, as String
     * @param instructor the instructor, as String
     * @param studio     the studio, as String
     * @param difficulty the difficulty, as String
     * @param equipment  the equipment, as List<String>
     * @param dateTime   the date time, as String
     * @param capacity   the capacity, as int
     * @param basePrice  the base price, as double
     */
    public GymClass(String name, String instructor, String studio,
        String difficulty, List<String> equipment,
        String dateTime, int capacity, double basePrice) {
        this.name = name;
        this.instructor = instructor;
        this.studio = studio;
        this.difficulty = difficulty;
        this.equipment = equipment;
        this.dateTime = dateTime;
        this.capacity = capacity;
        this.basePrice = basePrice;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets instructor.
     *
     * @return the instructor
     */
    public String getInstructor() {
        return instructor;
    }

    /**
     * Gets studio.
     *
     * @return the studio
     */
    public String getStudio() {
        return studio;
    }

    /**
     * Gets difficulty.
     *
     * @return the difficulty
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * Gets equipment.
     *
     * @return the equipment
     */
    public List<String> getEquipment() {
        return equipment;
    }

    /**
     * Gets date time.
     *
     * @return the date time
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * Gets spots remaining.
     *
     * @return the spots remaining
     */
    public int getSpotsRemaining() {
        return capacity - bookedMembers.size();
    }

    /**
     * Gets capacity.
     *
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Gets base price.
     *
     * @return the base price
     */
    public double getBasePrice() {
        return basePrice;
    }

    /**
     * Sets id.
     *
     * @param id the id, as int
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Book boolean.
     *
     * @param memberName the member name, as String
     * @return the boolean
     */
    public boolean book(String memberName) {
        if (bookedMembers.size() < capacity) {
            return bookedMembers.add(memberName);
        }
        return false; // Class is full
    }

    /**
     * Cancel boolean.
     *
     * @param memberName the member name, as String
     * @return the boolean
     */
    public boolean cancel(String memberName) {
        return bookedMembers.remove(memberName);
    }

    /**
     * Clone with new date gym class.
     *
     * @param newDateTime the new date time, as String
     * @return the gym class
     */
    public GymClass cloneWithNewDate(String newDateTime) {
        return new GymClass(name, instructor, studio, difficulty,
            new ArrayList<>(equipment), newDateTime, capacity, basePrice); // changes to the clone must not affect the original list of equipment
    }

    /**
     * Builder pattern: The toString method provides a detailed string representation of the GymClass, including all relevant information and the list of booked members.
     * This allows for easy display of class details when the "show <class id>" command is used.
     */
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("ID: ").append(id).append("\n");
      sb.append("Name: ").append(name).append("\n");
      sb.append("Instructor: ").append(instructor).append("\n");
      sb.append("Room: ").append(studio).append("\n");
      sb.append("Difficulty: ").append(difficulty).append("\n");
      sb.append("Equipment: ").append(String.join(", ", equipment)).append("\n");
      sb.append("Date/Time: ").append(dateTime).append("\n");
      sb.append("Capacity: ").append(getSpotsRemaining()).append("/").append(capacity).append("\n");
      sb.append("Booked: ").append(bookedMembers.isEmpty() ? "None" : String.join(", ", bookedMembers));
      return sb.toString();
    }
}
