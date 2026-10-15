// Apply the stored theme before painting. Shared by web and extension pages.
const media = matchMedia('(prefers-color-scheme: dark)');
const apply = () => {
  const mode = localStorage.getItem('autofirme-theme') ?? 'system';
  document.documentElement.classList.toggle('dark', mode === 'dark' || (mode === 'system' && media.matches));
};
apply();
media.addEventListener('change', apply);
