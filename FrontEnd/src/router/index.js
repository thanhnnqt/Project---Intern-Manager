import { createRouter, createWebHistory } from 'vue-router';
import LoginView from '../views/LoginView.vue';
import DashboardView from '../views/DashboardView.vue';
import InternList from '../views/interns/InternList.vue';
import InternCreate from '../views/interns/InternCreate.vue';
import InternEdit from '../views/interns/InternEdit.vue';
import InternDetail from '../views/interns/InternDetail.vue';
import MentorList from '../views/mentors/MentorList.vue';
import MentorCreate from '../views/mentors/MentorCreate.vue';
import MentorEdit from '../views/mentors/MentorEdit.vue';
import TaskList from '../views/tasks/TaskList.vue';
import TaskForm from '../views/tasks/TaskForm.vue';
import TaskDetail from '../views/tasks/TaskDetail.vue';
import ProfileView from '../views/profile/ProfileView.vue';
import NotificationView from '../views/notifications/NotificationView.vue';

import MainLayout from '../layouts/MainLayout.vue';

const routes = [
    {
        path: '/',
        redirect: '/dashboard'
    },
    {
        path: '/login',
        name: 'Login',
        component: LoginView,
        meta: { guest: true }
    },
    {
        path: '/',
        component: MainLayout,
        meta: { requiresAuth: true },
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: DashboardView
            },
            {
                path: 'interns',
                name: 'InternList',
                component: InternList,
                meta: { authorizedRoles: ['ADMIN', 'MENTOR'] }
            },
            {
                path: 'interns/create',
                name: 'InternCreate',
                component: InternCreate,
                meta: { authorizedRoles: ['ADMIN'] }
            },
            {
                path: 'interns/:id/edit',
                name: 'InternEdit',
                component: InternEdit,
                meta: { authorizedRoles: ['ADMIN', 'MENTOR'] }
            },
            {
                path: 'interns/:id',
                name: 'InternDetail',
                component: InternDetail,
                meta: { authorizedRoles: ['ADMIN', 'MENTOR'] }
            },
            {
                path: 'mentors',
                name: 'MentorList',
                component: MentorList,
                meta: { authorizedRoles: ['ADMIN'] }
            },
            {
                path: 'mentors/create',
                name: 'MentorCreate',
                component: MentorCreate,
                meta: { authorizedRoles: ['ADMIN'] }
            },
            {
                path: 'mentors/:id/edit',
                name: 'MentorEdit',
                component: MentorEdit,
                meta: { authorizedRoles: ['ADMIN'] }
            },
            {
                path: 'tasks',
                name: 'TaskList',
                component: TaskList
            },
            {
                path: 'tasks/create',
                name: 'TaskCreate',
                component: TaskForm,
                meta: { authorizedRoles: ['ADMIN', 'MENTOR'] }
            },
            {
                path: 'tasks/:id/edit',
                name: 'TaskEdit',
                component: TaskForm,
                meta: { authorizedRoles: ['ADMIN', 'MENTOR'] }
            },
            {
                path: 'tasks/:id',
                name: 'TaskDetail',
                component: TaskDetail
            },
            {
                path: 'profile',
                name: 'Profile',
                component: ProfileView
            },
            {
                path: 'notifications',
                name: 'Notifications',
                component: NotificationView
            }
        ]
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

// Navigation guard
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user') || 'null');

    if (to.meta.requiresAuth && !token) {
        // Redirect to login if token missing
        next({ name: 'Login' });
    } else if (to.meta.guest && token) {
        // Already logged in, don't show login page
        next({ name: 'Dashboard' });
    } else if (to.meta.authorizedRoles && user) {
        // Role check
        if (to.meta.authorizedRoles.includes(user.role)) {
            next();
        } else {
            console.warn(`Access denied to ${to.path} for role ${user.role}`);
            next({ name: 'Dashboard' });
        }
    } else {
        next();
    }
});

export default router;
