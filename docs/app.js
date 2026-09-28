const loginPage = document.querySelector('#loginPage');
const appPage = document.querySelector('#appPage');
const pageContent = document.querySelector('#pageContent');
const loginMessage = document.querySelector('#loginMessage');

const pages = {
    dashboard: {
        title: 'Good morning, Librarian',
        description: "Here is today's library overview.",
        html: `<div class="stats">
            <article class="stat"><small>Total Books</small><strong>248</strong><span>Available collection</span></article>
            <article class="stat"><small>Total Members</small><strong>96</strong><span>Registered readers</span></article>
            <article class="stat"><small>Borrowed Books</small><strong>32</strong><span>Currently on loan</span></article>
            <article class="stat"><small>Overdue Books</small><strong>05</strong><span>Need attention</span></article>
        </div><h3>Quick actions</h3><div class="actions"><button class="button primary" data-page="books">Add a Book</button><button class="button light" data-page="members">Register Member</button></div>`
    },
    books: { title: 'Add Book', description: 'Enter the details of a new book in the library.', html: bookForm() },
    members: { title: 'Add Member', description: 'Register a new library member.', html: memberForm() },
    manage: { title: 'Manage Members', description: 'Search, edit, or delete registered members.', html: `<input id="memberSearch" placeholder="Search members by name or ID"><table><thead><tr><th>Member ID</th><th>Full Name</th><th>Email</th><th>Phone</th></tr></thead><tbody id="memberRows"></tbody></table>` },
    issue: { title: 'Issue Book', description: 'Create a borrowing record for a member.', html: loanForm('Issue Book') },
    return: { title: 'Return Book', description: 'Select a borrowed book and record its return.', html: loanForm('Return Book') },
    history: { title: 'Borrowing History', description: 'Review borrowed, returned, and overdue books.', html: `<table><thead><tr><th>Member ID</th><th>Book Title</th><th>Issue Date</th><th>Due Date</th><th>Return Date</th><th>Status</th></tr></thead><tbody><tr><td>M-001</td><td>Clean Code</td><td>2026-09-15</td><td>2026-09-29</td><td>-</td><td>Borrowed</td></tr><tr><td>M-002</td><td>Java Basics</td><td>2026-09-01</td><td>2026-09-15</td><td>2026-09-14</td><td>Returned</td></tr><tr><td>M-003</td><td>Effective Java</td><td>2026-08-20</td><td>2026-09-03</td><td>-</td><td>Overdue</td></tr></tbody></table>`
    }
};

const members = [
    ['M-001', 'Amal Perera', 'amal@email.com', '077 123 4567'],
    ['M-002', 'Nimal Silva', 'nimal@email.com', '071 555 2211'],
    ['M-003', 'Sara Fernando', 'sara@email.com', '076 900 1122']
];

function bookForm() { return `<div class="panel form"><label>Book ID / ISBN<input placeholder="Book ID or ISBN"></label><label>Book Title<input placeholder="Book title"></label><label>Author<input placeholder="Author name"></label><label>Category<input placeholder="Category"></label><label>Published Year<input placeholder="Published year"></label><label>Quantity<input type="number" min="1" placeholder="Quantity"></label><button class="button primary" data-message="Book added successfully.">Add Book</button><button class="button light" type="reset">Clear</button></div>`; }
function memberForm() { return `<div class="panel form"><label>Member ID<input placeholder="Member ID"></label><label>Full Name<input placeholder="Full name"></label><label>Email<input type="email" placeholder="Email address"></label><label>Phone Number<input placeholder="Phone number"></label><label>Address<input placeholder="Address"></label><button class="button primary" data-message="Member registered successfully.">Register Member</button></div>`; }
function loanForm(buttonText) { return `<div class="panel form"><label>Select Member<select><option>Select member</option><option>M-001 - Amal Perera</option><option>M-002 - Nimal Silva</option></select></label><label>Select Book<select><option>Select book</option><option>B-101 - Clean Code</option><option>B-102 - Java Basics</option></select></label><label>Issue Date<input type="date" value="2026-09-29"></label><label>Due Date<input type="date" value="2026-10-13"></label><button class="button primary" data-message="${buttonText} saved successfully.">${buttonText}</button></div>`; }

function showPage(name) {
    const page = pages[name];
    pageContent.innerHTML = `<h2>${page.title}</h2><p class="description">${page.description}</p>${page.html}`;
    document.querySelectorAll('nav button').forEach(button => button.classList.toggle('active', button.dataset.page === name));
    if (name === 'manage') fillMembers();
}

function fillMembers() {
    const rows = document.querySelector('#memberRows');
    const search = document.querySelector('#memberSearch');
    const draw = () => { rows.innerHTML = members.filter(member => member.join(' ').toLowerCase().includes(search.value.toLowerCase())).map(member => `<tr>${member.map(value => `<td>${value}</td>`).join('')}</tr>`).join(''); };
    search.addEventListener('input', draw); draw();
}

document.querySelector('#loginForm').addEventListener('submit', event => {
    event.preventDefault();
    if (!event.target.username.value.trim() || !event.target.password.value) { loginMessage.textContent = 'Please enter both username and password.'; return; }
    loginPage.classList.add('hidden'); appPage.classList.remove('hidden'); showPage('dashboard');
});
document.querySelector('#loginForm').addEventListener('reset', () => loginMessage.textContent = '');
document.addEventListener('click', event => {
    const pageButton = event.target.closest('[data-page]');
    if (pageButton) showPage(pageButton.dataset.page);
    if (event.target.dataset.message) alert(event.target.dataset.message);
});
document.querySelector('#logoutButton').addEventListener('click', () => { appPage.classList.add('hidden'); loginPage.classList.remove('hidden'); });
