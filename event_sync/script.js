function addEvent() {
    const title = document.getElementById("title").value;
    const description = document.getElementById("description").value;
    const event_time = document.getElementById("event_time").value;

    fetch("add_event.php", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `title=${title}&description=${description}&event_time=${event_time}`
    })
    .then(response => response.text())
    .then(data => {
        loadEvents();
    });
}

function loadEvents() {
    fetch("fetch_events.php")
    .then(response => response.text())
    .then(data => {
        document.getElementById("eventList").innerHTML = data;
    });
}

function deleteEvent(id) {
    fetch("delete_event.php", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `id=${id}`
    })
    .then(response => response.text())
    .then(data => {
        loadEvents();
    });
}
// Auto refresh every 3 seconds
setInterval(loadEvents, 3000);

loadEvents();