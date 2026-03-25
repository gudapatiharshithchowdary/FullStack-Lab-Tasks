<?php
include 'db.php';

$result = $conn->query("SELECT * FROM events ORDER BY created_at DESC");

if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        echo "<div class='event'>";
        echo "<b>" . $row['title'] . "</b><br>";
        echo $row['description'] . "<br>";
        echo $row['event_time'] . "<br>";

        // ✅ DELETE BUTTON
        echo "<button onclick='deleteEvent(" . $row['id'] . ")'>Delete</button>";

        echo "</div><hr>";
    }
} else {
    echo "No events found";
}

$conn->close();
?>