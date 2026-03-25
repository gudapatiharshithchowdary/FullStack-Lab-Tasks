<?php
include 'db.php';

$title = $_POST['title'];
$description = $_POST['description'];
$event_time = $_POST['event_time'];

$sql = "INSERT INTO events (title, description, event_time)
        VALUES ('$title', '$description', '$event_time')";

if ($conn->query($sql) === TRUE) {
    echo "Event Added";
} else {
    echo "Error: " . $conn->error;
}

$conn->close();
?>