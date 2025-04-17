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
    let tbody = document.getElementById("userTableBody");
    for (let i = 0; i < 10; i++) {
        let row = document.createElement("tr");
        tbody.appendChild(row);

        for (let j = 0; j < 3; j++) {
            let td = document.createElement('td')
            td.id = `Row ${i} Cell ${j}`
            td.innerHTML = `Row ${i} Cell ${j}`;
            row.appendChild(td);
        }

        let deleteBtnBox = document.createElement("td");
        let deleteButton = document.createElement("button");
        deleteButton.className = "btn btn-danger btn-sm";
        deleteButton.textContent = 'Delete';
        deleteButton.type = "button";
        deleteBtnBox.append(deleteButton);
        row.appendChild(deleteBtnBox);

        let editBtnBox = document.createElement("td");
        let editButton = document.createElement("button");
        editButton.className = "btn btn-primary btn-sm";
        editButton.textContent = 'Edit';
        editButton.type = "button";

        let saveBtn = document.createElement("button");
        saveBtn.className = "btn btn-success btn-sm save-btn d-none";
        saveBtn.textContent = 'Save';
        saveBtn.type = "button";

        editButton.addEventListener("click", () => {
            let roleCell = document.getElementById(`Row ${i} Cell 0`);
            let priorityCell = document.getElementById(`Row ${i} Cell 1`);
            let adminCell = document.getElementById(`Row ${i} Cell 2`);

            console.log(roleCell);
            console.log(priorityCell);
            console.log(adminCell);

            // Converti i campi in input
            const roleInput = document.createElement("input");
            roleInput.type = "text";
            roleInput.value = roleCell.innerHTML;
            roleCell.innerHTML = ''
            roleCell.appendChild(roleInput);

            const priorityInput = document.createElement("input");
            priorityInput.type = "text";
            priorityInput.value = priorityCell.innerHTML;
            priorityCell.innerHTML = ''
            priorityCell.appendChild(priorityInput);

            const adminInput = document.createElement("input");
            adminInput.type = "text";
            adminInput.value = adminCell.innerHTML;
            adminCell.innerHTML = '';
            adminCell.appendChild(adminInput);

            // Nascondi il pulsante Edit e mostrare il salvataggio
            editButton.classList.add('d-none');
            saveBtn.classList.remove('d-none');

            // Gestire il salvataggio
            saveBtn.addEventListener("click", () => {
                roleCell.innerHTML = roleInput.value;
                priorityCell.innerHTML = priorityInput.value;
                adminCell.innerHTML = adminInput.value;

                saveBtn.classList.add('d-none');
                editButton.classList.remove('d-none');
            })
        })

        editBtnBox.append(editButton);
        editBtnBox.append(saveBtn);
        row.appendChild(editBtnBox);
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
    let tbody = document.getElementById("userTableBody");
    for (let i = 0; i < 10; i++) {
        let row = document.createElement("tr");
        tbody.appendChild(row);

        for (let j = 0; j < 3; j++) {
            let td = document.createElement('td')
            td.id = `Row ${i} Cell ${j}`
            td.innerHTML = `Row ${i} Cell ${j}`;
            row.appendChild(td);
        }

        let deleteBtnBox = document.createElement("td");
        let deleteButton = document.createElement("button");
        deleteButton.className = "btn btn-danger btn-sm";
        deleteButton.textContent = 'Delete';
        deleteButton.type = "button";
        deleteBtnBox.append(deleteButton);
        row.appendChild(deleteBtnBox);

        let editBtnBox = document.createElement("td");
        let editButton = document.createElement("button");
        editButton.className = "btn btn-primary btn-sm";
        editButton.textContent = 'Edit';
        editButton.type = "button";

        let saveBtn = document.createElement("button");
        saveBtn.className = "btn btn-success btn-sm save-btn d-none";
        saveBtn.textContent = 'Save';
        saveBtn.type = "button";

        editButton.addEventListener("click", () => {
            let roleCell = document.getElementById(`Row ${i} Cell 0`);
            let priorityCell = document.getElementById(`Row ${i} Cell 1`);
            let adminCell = document.getElementById(`Row ${i} Cell 2`);

            console.log(roleCell);
            console.log(priorityCell);
            console.log(adminCell);

            // Converti i campi in input
            const roleInput = document.createElement("input");
            roleInput.type = "text";
            roleInput.value = roleCell.innerHTML;
            roleCell.innerHTML = ''
            roleCell.appendChild(roleInput);

            const priorityInput = document.createElement("input");
            priorityInput.type = "text";
            priorityInput.value = priorityCell.innerHTML;
            priorityCell.innerHTML = ''
            priorityCell.appendChild(priorityInput);

            const adminInput = document.createElement("input");
            adminInput.type = "text";
            adminInput.value = adminCell.innerHTML;
            adminCell.innerHTML = '';
            adminCell.appendChild(adminInput);

            // Nascondi il pulsante Edit e mostrare il salvataggio
            editButton.classList.add('d-none');
            saveBtn.classList.remove('d-none');

            // Gestire il salvataggio
            saveBtn.addEventListener("click", () => {
                roleCell.innerHTML = roleInput.value;
                priorityCell.innerHTML = priorityInput.value;
                adminCell.innerHTML = adminInput.value;

                saveBtn.classList.add('d-none');
                editButton.classList.remove('d-none');
            })
        })

        editBtnBox.append(editButton);
        editBtnBox.append(saveBtn);
        row.appendChild(editBtnBox);
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
    let tbody = document.getElementById("userTableBody");
    for (let i = 0; i < 10; i++) {
        let row = document.createElement("tr");
        tbody.appendChild(row);

        for (let j = 0; j < 4; j++) {
            let td = document.createElement('td')
            td.id = `Row ${i} Cell ${j}`
            td.innerHTML = `Row ${i} Cell ${j}`;
            row.appendChild(td);
        }

        let deleteBtnBox = document.createElement("td");
        let deleteButton = document.createElement("button");
        deleteButton.className = "btn btn-danger btn-sm";
        deleteButton.textContent = 'Delete';
        deleteButton.type = "button";
        deleteBtnBox.append(deleteButton);
        row.appendChild(deleteBtnBox);

        let editBtnBox = document.createElement("td");
        let editButton = document.createElement("button");
        editButton.className = "btn btn-primary btn-sm";
        editButton.textContent = 'Edit';
        editButton.type = "button";

        let saveBtn = document.createElement("button");
        saveBtn.className = "btn btn-success btn-sm save-btn d-none";
        saveBtn.textContent = 'Save';
        saveBtn.type = "button";

        editButton.addEventListener("click", () => {
            let emailCell = document.getElementById(`Row ${i} Cell 0`);
            let nameCell = document.getElementById(`Row ${i} Cell 1`);
            let surnameCell = document.getElementById(`Row ${i} Cell 2`);
            let roleCell = document.getElementById(`Row ${i} Cell 3`);

            // Converti i campi in input
            const emailInput = document.createElement("input");
            emailInput.type = "text";
            emailInput.value = emailCell.innerHTML;
            emailCell.innerHTML = ''
            emailCell.appendChild(emailInput);

            const nameInput = document.createElement("input");
            nameInput.type = "text";
            nameInput.value = nameCell.innerHTML;
            nameCell.innerHTML = ''
            nameCell.appendChild(nameInput);

            const surnameInput = document.createElement("input");
            surnameInput.type = "text";
            surnameInput.value = surnameCell.innerHTML;
            surnameCell.innerHTML = '';
            surnameCell.appendChild(surnameInput);

            const roleInput = document.createElement("input");
            roleInput.type = "text";
            roleInput.value = roleCell.innerHTML;
            roleCell.innerHTML = '';
            roleCell.appendChild(roleInput);

            // Nascondi il pulsante Edit e mostrare il salvataggio
            editButton.classList.add('d-none');
            saveBtn.classList.remove('d-none');

            // Gestire il salvataggio
            saveBtn.addEventListener("click", () => {
                emailCell.innerHTML = emailInput.value;
                nameCell.innerHTML = nameInput.value;
                surnameCell.innerHTML = surnameInput.value;
                roleCell.innerHTML = roleInput.value;

                saveBtn.classList.add('d-none');
                editButton.classList.remove('d-none');
            })
        })

        editBtnBox.append(editButton);
        editBtnBox.append(saveBtn);
        row.appendChild(editBtnBox);
    }
}


function createUser() {
    const main = document.getElementById("main");
    main.innerHTML = `<form id="userForm" method="POST" class="was-validated" action="/createUser" enctype="multipart/form-data">
    <div class="row p-5">
        <div class="col">
            <label for="mail">Mail:</label><br>
            <input type="text" id="mail" name="mail" required><br>
            <div class="invalid-feedback">
              il campo è obbligatorio
            </div>

            <label for="name">Name:</label><br>
            <input type="text" id="name" name="name"  required><br>
            <div class="invalid-feedback">
              il campo è obbligatorio
            </div>

            <label for="password">Password:</label><br>
            <input type="password" id="password" name="password" required><br>
            <div class="invalid-feedback">
              il campo è obbligatorio
            </div>
        </div>

        <div class="col">

            <label for="surname">Surname:</label><br>
            <input type="text" id="surname" name="surname" required><br>
            <div class="invalid-feedback">
              il campo è obbligatorio
            </div><br>
            <div class="row">
            <label for="group">Group:</label><br>
                <div class="col m-1 pr-0">
                    <input type="text" id="group" name="group" readonly ><br>
            <div class="invalid-feedback" id="inputError">
              il campo è obbligatorio
            </div>

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
    const groupInput = document.getElementById("group");
    if(groupInput.value.trim()== "") {
        const groupIdError = document.getElementById("inputError");
        groupIdError.classList.add("d-block");
    }
    createDropdown(document.getElementById("dropdownUser"), [
        {name: "Group 1"},
        {name: "Group 2"},
        {name: "Group 3"},
        {name: "Group 4"},
        {name: "Group 5"},
        {name: "Group 6"},
    ],groupInput);
}

function createGroup() {
    const main = document.getElementById("main");
    main.innerHTML = `<form id="userForm" method="POST" action="/createUser" class="was-validated" enctype="multipart/form-data">
    <div class="row p-5">

        <div class="row">
            <div class="col-3 m-1">
                <label for="role">Role:</label><br>
                <input type="text" id="role" name="role" readonly><br>
                <div class="invalid-feedback" id="inputError">
                  il campo è obbligatorio
                </div>
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

        const groupInput = document.getElementById("role");
        if(groupInput.value.trim()== "") {
            const groupIdError = document.getElementById("inputError");
            groupIdError.classList.add("d-block");
        }
    createDropdown(document.getElementById("dropdownGroup"), [
        {name: "Group 1"},
        {name: "Group 2"},
        {name: "Group 3"},
        {name: "Group 4"},
        {name: "Group 5"},
        {name: "Group 6"},
    ],groupInput);
}

function createRole() {
    const main = document.getElementById("main");
    main.innerHTML = `<form id="userForm" method="POST" action="/createUser"  class="was-validated" enctype="multipart/form-data">


        <div class="row">
            <div class="col-3 m-1">
                <label for="role">Role:</label><br>
                <input type="text" id="role" name="role" required><br>
                <div class="invalid-feedback" >
                  il campo è obbligatorio
                </div>
                <label for="priority">Priority:</label><br>
                <input type="number" id="priority" name="priority" required><br>
                <div class="invalid-feedback" >
                    il campo è obbligatorio
                </div>
            </div>
            
            <div class="col-3 m-1">
                <label for="admin">Admin:</label><br>
                <input type="checkbox" id="admin" name="admin" ><br>
                <input type="submit" class="mt-3" value="Create Role">
            </div>
         </div>
</form>
        `;
}

function createDropdown(elem, data,input) {
    data.forEach((item) => {
        li = document.createElement("li");
        a = document.createElement("a");
        a.classList.add("dropdown-item");
        li.appendChild(a);
        a.innerHTML = item.name;
        elem.appendChild(li);
        a.addEventListener("click", () => {
            input.value = item.name;
            const groupIdError = document.getElementById("inputError");
            groupIdError.classList.remove("d-block");
        })
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