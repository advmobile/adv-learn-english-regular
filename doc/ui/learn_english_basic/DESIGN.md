---
name: Learn English Basic
colors:
  surface: '#f9f9ff'
  surface-dim: '#d3daef'
  surface-bright: '#f9f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f1f3ff'
  surface-container: '#e9edff'
  surface-container-high: '#e1e8fd'
  surface-container-highest: '#dce2f7'
  on-surface: '#141b2b'
  on-surface-variant: '#3c4a42'
  inverse-surface: '#293040'
  inverse-on-surface: '#edf0ff'
  outline: '#6c7a71'
  outline-variant: '#bbcabf'
  surface-tint: '#006c49'
  primary: '#006c49'
  on-primary: '#ffffff'
  primary-container: '#10b981'
  on-primary-container: '#00422b'
  inverse-primary: '#4edea3'
  secondary: '#855300'
  on-secondary: '#ffffff'
  secondary-container: '#fea619'
  on-secondary-container: '#684000'
  tertiary: '#006a63'
  on-tertiary: '#ffffff'
  tertiary-container: '#5bb1a8'
  on-tertiary-container: '#00413c'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#6ffbbe'
  primary-fixed-dim: '#4edea3'
  on-primary-fixed: '#002113'
  on-primary-fixed-variant: '#005236'
  secondary-fixed: '#ffddb8'
  secondary-fixed-dim: '#ffb95f'
  on-secondary-fixed: '#2a1700'
  on-secondary-fixed-variant: '#653e00'
  tertiary-fixed: '#9cf2e8'
  tertiary-fixed-dim: '#80d5cb'
  on-tertiary-fixed: '#00201d'
  on-tertiary-fixed-variant: '#00504a'
  background: '#f9f9ff'
  on-background: '#141b2b'
  surface-variant: '#dce2f7'
  mint-bg: '#F0FDF4'
  surface-white: '#FFFFFF'
  solar-gold: '#FFB300'
  teal-dark: '#0F766E'
  feedback-error: '#EF4444'
  feedback-info: '#3B82F6'
  feedback-success: '#10B981'
  deep-forest: '#064E3B'
typography:
  display-hero:
    fontFamily: Plus Jakarta Sans
    fontSize: 36px
    fontWeight: '800'
    lineHeight: 44px
    letterSpacing: -0.03em
  display-hero-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '800'
    lineHeight: 34px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '800'
    lineHeight: 30px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '700'
    lineHeight: 26px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '700'
    lineHeight: 22px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 15px
    fontWeight: '400'
    lineHeight: 22px
  body-sm:
    fontFamily: Inter
    fontSize: 13px
    fontWeight: '500'
    lineHeight: 18px
  label-ipa:
    fontFamily: Space Mono
    fontSize: 15px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0.02em
  label-badge:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.04em
  label-caption:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 14px
    letterSpacing: 0.02em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1.25rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

This design system delivers a **Modern Editorial meets Tactile Gamified** mobile experience tailored for beginner language learners (A1–A2). It reconciles the clean, authoritative readability of contemporary editorial typography with the delightful, low-friction charm of gamified educational interfaces. The emotional objective is to remove intimidation, replace foreign-language friction with clarity, and instill a daily habit loop driven by warmth, optimism, and visible progress.

### Visual Aesthetic & Movement
- **Modern Editorial Foundations:** Large, expressive display typography using bold sans-serif geometric headlines contrasted with utilitarian, high-legibility body type and crisp monospaced IPA phonetic notation.
- **Tactile & Clean Gamification:** Generous corner radii (`24px` / `rounded-3xl`), soft ambient drop shadows, and subtle chromatic depth gradients that invite direct physical touch without descending into cluttered cartoonism.
- **Airy Mint Atmosphere:** Light, refreshing background tones inspired by clean mint leaves and morning study sessions, keeping eye strain minimal during long practice runs.

## Colors

The color palette is built around nature, freshness, and achievement. 

- **Primary (`#10B981` Emerald Green):** Represents growth, renewal, and linguistic confidence. Used for primary CTAs, active pathway nodes, and correct answer states. It frequently pairs with `#0F766E` (Teal 700) to form a directional `135deg` ambient gradient across milestone surfaces.
- **Secondary (`#F59E0B` Warm Amber / `#FFB300` Solar Gold):** Dedicated to high-dopamine gamification elements: streak counts 🔥, XP accumulation bars, star ratings, and achievement crests.
- **Tertiary (`#0F766E` Deep Teal):** Provides visual anchoring for elevated text, active tab icons, and progress tracks, grounding the lighter emerald tones.
- **Neutral (`#111827` Slate Black):** High-contrast, editorial ink color for foundational text, ensuring complete legibility on mobile viewports.
- **Surface Roles:** The canonical backdrop is Mint Clean (`#F0FDF4`), paired with pristine `#FFFFFF` card layers to create natural visual elevation without relying on heavy borders.

## Typography

The typographic hierarchy implements three distinct typefaces to fulfill discrete educational tasks:

1. **Plus Jakarta Sans (Headings & Metrics):** Chosen for its punchy, friendly geometric curves and open counters. Set in weight `800` (ExtraBold) or `700` (Bold) for unit headers, lesson titles, and numerical streak badges.
2. **Inter (Instructional & Editorial Body):** Ensures crisp readability across screen resolutions, providing neutral rendering for translations, context paragraphs, and multiple-choice answers.
3. **Space Mono (IPA Pronunciation & Phonetics):** Monospaced precision assigned specifically to International Phonetic Alphabet (IPA) strings (e.g., `[həˈloʊ]`). The monospace structure visually flags phonetic syntax as a distinct linguistic tool.

## Layout & Spacing

This design system adheres to an 8pt-based fluid mobile grid designed primarily for standard touchscreens (375px to 430px wide), constrained to a maximum content width of 480px on larger tablets and desktop viewports.

- **Canvas Gutters & Margins:** Edge margins are pegged at `1.25rem` (20px) on mobile viewports to preserve thumb comfort while maximizing reading width. Internal grid gutters sit at `1rem` (16px).
- **Vertical Rhythm:** Sections and learning clusters stack with `space-xl` (32px) margins to cleanly segregate content blocks (e.g., Foundations vs. Daily Routines).
- **Dock Safe Areas:** Bottom navigation reserves `84px` plus safe-area insets, preventing cards and CTA triggers from overlapping the floating dock.

## Elevation & Depth

Visual depth is achieved through **ambient tinting and tactile layering**, bypassing harsh drop shadows in favor of organic, friendly lift:

- **Level 0 (Flat / Canvas):** Mint Clean (`#F0FDF4`) base canvas with zero elevation.
- **Level 1 (Card & Module Surfaces):** Pure White (`#FFFFFF`) with a dual shadow: `0 4px 20px -2px rgba(16, 185, 129, 0.08), 0 2px 6px -1px rgba(0, 0, 0, 0.04)`. This subtle emerald-tinted shadow anchors cards into the mint environment.
- **Level 2 (Interactive Modules & Flashcards):** `0 12px 28px -4px rgba(16, 185, 129, 0.14), 0 4px 10px -2px rgba(0, 0, 0, 0.05)`, communicating that an element can be tapped, flipped, or dragged.
- **Level 3 (Floating Docks & Sticky Modals):** Translucent backdrop blur (`backdrop-blur-xl`, `rgba(255, 255, 255, 0.85)`) coupled with a faint ring outline (`1px solid rgba(16, 185, 129, 0.12)`) and deep shadow `0 20px 32px -8px rgba(15, 118, 110, 0.16)`.

## Shapes

The design system adopts an overtly rounded, tactile aesthetic:

- **Primary Cards & Containers:** Standardized at `24px` (`rounded-3xl` equivalent) to yield a soft, friendly hand-held feel.
- **Buttons & Interactive Tags:** Pill-shaped (`9999px` / `rounded-full`) for high-order actions, search pills, and floating docks.
- **Form Controls & Options:** `16px` (`rounded-2xl`) for selectable quiz option tiles, ensuring enough internal curvature to match card envelopes.

## Components

### Buttons & Interactive Triggers
- **Primary Action (CTA):** Full-width or auto-layout pill (`rounded-full`) painted with an active gradient (`from-emerald-500 to-teal-600`), white bold text, subtle inner top highlight (`inset 0 1px 0 rgba(255,255,255,0.3)`), and a physical 3D base bevel (`border-b-4 border-teal-800`).
- **Secondary / Ghost Button:** Transparent background, `1.5px` border in `#10B981`, bold emerald text, scaling down to 96% on active press.
- **Audio Playback Pill:** Circular or pill button with `#10B981` tint, containing a prominent speaker icon and animated pulse waves.

### Chips & Stat Badges
- **Streak 🔥 Chip:** Amber tint background (`#FEF3C7`), text color `#B45309`, rounded-full with an embedded fire icon and bold day count.
- **XP Metric Badge:** Solar Gold gradient fill with crisp white text and a mini star icon.

### Form Inputs & Quiz Option Selectors
- **Quiz Choice Cards:** `24px` rounded white surfaces with a neutral border (`1.5px solid #E5E7EB`). In selected state, transitions to emerald tint (`#ECFDF5`), emerald border (`2px solid #10B981`), and shows a checkmark icon indicator.
- **Fill-in-the-Blank Fields:** Underlined or individual pill boxes with `#10B981` focus rings, monospaced placeholder hints, and automatic error shakes on incorrect entries.

### Educational & Gamified Modules
- **Learning Path Nodes (Curriculum Tree):** Large circular nodes (`64px` diameter) with thick 3D bottom bevels, centered skill iconography, and 1 to 3 trailing gold stars underneath indicating completion mastery.
- **3D Flip Flashcards:** Centered perspective containers with double-sided rendering. Face side displays target English vocabulary, Space Mono IPA transcription, and speaker trigger. Reverse side details translation, sample sentences, and SRS rating triggers (`Again`, `Hard`, `Good`, `Easy`).
- **Floating Bottom Dock:** Pill container floating 16px above screen bottom, finished in semi-transparent white frost (`backdrop-blur-xl`), housing four balanced navigation icons with emerald active indicators.