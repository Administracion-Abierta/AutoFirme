import { mount } from 'svelte';
import './app.css';
import Page from '../src/routes/+page.svelte';

mount(Page, { target: /** @type {HTMLElement} */ (document.getElementById('app')) });
