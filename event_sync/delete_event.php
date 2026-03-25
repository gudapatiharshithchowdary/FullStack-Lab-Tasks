<?php
include 'db.php';

$id = $_POST['id'];

$sql = "DELETE FROM events WHERE id=$id";

if ($conn->query($sql) === TRUE) {
    echo "Deleted successfully";
} else {
    echo "Error: " . $conn->error;
}

$conn->close();
?>