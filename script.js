
function showGroups() {
    const main = document.getElementById("main");
    main.innerHTML = `<table class="table table-striped table-hover table-bordered table-sm">
    <thead class="table-dark">
        <tr>
            <th scope="col">Role</th>
            <th scope="col">Priority</th>
            <th scope="col">Admin</th>
            <th scope="col">Delete</th>
            <th scope="col">Edit</th>
        </tr>
    </thead>
    <tbody id="userTableBody">
    </tbody>
</table>`;
    tbody = document.getElementById("userTableBody");
    for (let i = 0; i < 10; i++) {
        row = tbody.insertRow();
        for (let j = 0; j < 3; j++) {
            cell = row.insertCell(j);
            cell.innerHTML = `Row ${i} Cell ${j}`;
        }
        cell = row.insertCell(3);
        cell.innerHTML = `<button type="button" class="btn btn-danger btn-sm">Delete</button>`;
        cell = row.insertCell(4);
        cell.innerHTML = `<button type="button" class="btn btn-primary btn-sm">Edit</button>`;

    }
}



function showRoles() {
    const main = document.getElementById("main");
    main.innerHTML = `<table class="table table-striped table-hover table-bordered table-sm">
    <thead class="table-dark">
        <tr>
            <th scope="col">Role</th>
            <th scope="col">Priority</th>
            <th scope="col">Admin</th>
            <th scope="col">Delete</th>
            <th scope="col">Edit</th>
        </tr>
    </thead>
    <tbody id="userTableBody">
    </tbody>
</table>`;
    tbody = document.getElementById("userTableBody");
    for (let i = 0; i < 10; i++) {
        row = tbody.insertRow();
        for (let j = 0; j < 3; j++) {
            cell = row.insertCell(j);
            cell.innerHTML = `Row ${i} Cell ${j}`;
        }
        cell = row.insertCell(3);
        cell.innerHTML = `<button type="button" class="btn btn-danger btn-sm">Delete</button>`;
        cell = row.insertCell(4);
        cell.innerHTML = `<button type="button" class="btn btn-primary btn-sm">Edit</button>`;

    }
}





function showUsers() {
    const main = document.getElementById("main");
    main.innerHTML = `<table class="table table-striped table-hover table-bordered table-sm">
    <thead class="table-dark">
        <tr>
            <th scope="col">Mail</th>
            <th scope="col">Name</th>
            <th scope="col">Surname</th>
            <th scope="col">Role</th>
            <th scope="col">Delete</th>
            <th scope="col">Edit</th>
        </tr>
    </thead>
    <tbody id="userTableBody">
    </tbody>
</table>`;
    tbody = document.getElementById("userTableBody");
    for (let i = 0; i < 10; i++) {
        row = tbody.insertRow();
        for (let j = 0; j < 4; j++) {
            cell = row.insertCell(j);
            cell.innerHTML = `Row ${i} Cell ${j}`;
        }
        cell = row.insertCell(4);
        cell.innerHTML = `<button type="button" class="btn btn-danger btn-sm">Delete</button>`;
        cell = row.insertCell(5);
        cell.innerHTML = `<button type="button" class="btn btn-primary btn-sm">Edit</button>`;

    }
}



function createUser() {
    const main = document.getElementById("main");
    main.innerHTML = `<form id="userForm" method="POST" action="/createUser" enctype="multipart/form-data">
    <div class="row p-5">
        <div class="col">
            <label for="mail">Mail:</label><br>
            <input type="text" id="mail" name="mail"><br>

            <label for="name">Name:</label><br>
            <input type="text" id="name" name="name"><br>

            <label for="password">Password:</label><br>
            <input type="password" id="password" name="password"><br>
        </div>

        <div class="col">

            <label for="surname">Surname:</label><br>
            <input type="text" id="surname" name="surname"><br>
            <div class="row">
            <label for="group">Group:</label><br>
                <div class="col m-1 pr-0">
                    <input type="text" id="group" name="group" readonly>

                    <input type="submit" class="mt-3" value="Create User">
                </div>
                <div class="col m-1 p-0">
                    <a class="nav-link dropdown-toggle  p-1" href="#" role="button" data-bs-toggle="dropdown"
                        data-bs-display="static">
                        <i class="bi bi-bootstrap"></i>
                    </a>
                    <ul class="dropdown-menu position-static rounded-0 p-0" id="dropdownUser">
                    </ul>
                </div>
            </div>
        </div>
    </div>
</form>
        `;
    createDropdown(document.getElementById("dropdownUser"), [
        { name: "Group 1" },
        { name: "Group 2" },
        { name: "Group 3" },
        { name: "Group 4" },
        { name: "Group 5" },
        { name: "Group 6" },
    ]);
}

function createGroup() {
    const main = document.getElementById("main");
    main.innerHTML = `<form id="userForm" method="POST" action="/createUser" enctype="multipart/form-data">
    <div class="row p-5">

        <div class="row">
            <div class="col-3 m-1">
                <label for="role">Role:</label><br>
                <input type="text" id="role" name="role" readonly><br>
                <input type="submit" class="mt-3" value="Create Group">
            </div>
            <div class="col-8 m-1">
                <a class="nav-link dropdown-toggle mt-3 p-1" href="#" role="button" data-bs-toggle="dropdown"
                    data-bs-display="static">
                    <i class="bi bi-bootstrap"></i>
                </a>
                <ul class="dropdown-menu position-static rounded-0 p-0" id="dropdownGroup">
                </ul>
            </div>
        </div>
</form>
        `;

    createDropdown(document.getElementById("dropdownGroup"), [
        { name: "Group 1" },
        { name: "Group 2" },
        { name: "Group 3" },
        { name: "Group 4" },
        { name: "Group 5" },
        { name: "Group 6" },
    ]);
}

function createRole() {
    const main = document.getElementById("main");
    main.innerHTML = `<form id="userForm" method="POST" action="/createUser" enctype="multipart/form-data">


        <div class="row">
            <div class="col-3 m-1">
                <label for="role">Role:</label><br>
                <input type="text" id="role" name="role"><br>
                <label for="priority">Priority:</label><br>
                <input type="number" id="priority" name="priority"><br>
            </div>
            
            <div class="col-3 m-1">
                <label for="admin">Admin:</label><br>
                <input type="checkbox" id="admin" name="admin"><br>
                <input type="submit" class="mt-3" value="Create Role">
            </div>
         </div>
</form>
        `;
}

function createDropdown(elem, data) {
    data.forEach((item) => {
        li = document.createElement("li");
        a = document.createElement("a");
        a.classList.add("dropdown-item");
        li.appendChild(a);
        a.innerHTML = item.name;
        elem.appendChild(li);
    })
}

document.addEventListener("DOMContentLoaded", function () {
    const createUserB = document.getElementById("createUser");
    const createGroupB = document.getElementById("createGroup");
    const createRoleB = document.getElementById("createRole");
    const showUsersB = document.getElementById("showUsers");
    const showGroupsB = document.getElementById("showGroups");
    const showRolesB = document.getElementById("showRoles");

    createUserB.addEventListener("click", () => createUser());
    createGroupB.addEventListener("click", () => createGroup());
    createRoleB.addEventListener("click", () => createRole());
    showUsersB.addEventListener("click", () => showUsers());
    showGroupsB.addEventListener("click", () => showGroups());
    showRolesB.addEventListener("click", () => showRoles());



});