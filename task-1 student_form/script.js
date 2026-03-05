const form = document.getElementById("studentForm");
const tableBody = document.querySelector("#studentTable tbody");

function loadStudents() {
let students = JSON.parse(localStorage.getItem("students")) || [];
tableBody.innerHTML = "";

students.forEach(s => {
let row = `<tr>
<td>${s.name}</td>
<td>${s.email}</td>
<td>${s.dob}</td>
<td>${s.department}</td>
<td>${s.phone}</td>
</tr>`;
tableBody.innerHTML += row;
});
}

form.addEventListener("submit", function(e) {
e.preventDefault();

let student = {
name: document.getElementById("name").value,
email: document.getElementById("email").value,
dob: document.getElementById("dob").value,
department: document.getElementById("department").value,
phone: document.getElementById("phone").value
};

let students = JSON.parse(localStorage.getItem("students")) || [];
students.push(student);
localStorage.setItem("students", JSON.stringify(students));

form.reset();
loadStudents();
});

loadStudents();
