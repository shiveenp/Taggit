import Vue from 'vue'
import Router from 'vue-router'
import Login from '../components/Login'
import Home from "../components/Home";

Vue.use(Router);

export const router = new Router({
  mode: 'history',
  routes: [
    {path: '/', component: Login},
    {path: '/home', component: Home},

    // otherwise redirect to home
    {path: '*', redirect: '/'}
  ]
});

// router.beforeEach((to, from, next) => {
//   // redirect to login page if not logged in and trying to access a restricted page
//   const publicPages = ['/']
//   const authRequired = !publicPages.includes(to.path)
//   const loggedIn = localStorage.getItem('user')
//
//   if (authRequired && !loggedIn) {
//     return next('/')
//   }
//
//   next()
// })
