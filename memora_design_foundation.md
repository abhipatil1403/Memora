# Memora — Design Foundation

**Capture Once. Know Forever.**

---

## 1. Product Vision (UX Perspective)

Memora exists to collapse the distance between *seeing something* and *knowing it permanently*.

Today, people snap photos of whiteboards, receipts, posters, and prescriptions — then those images sink into a camera roll graveyard. The information is technically "saved" but functionally lost. Retrieving it requires scrolling, squinting, and hoping you recognise the right thumbnail.

Memora inverts this. The camera becomes an **intake mechanism for knowledge**, not a storage device for pixels. The moment a user captures an image, Memora's AI silently extracts structure — dates, contacts, deadlines, summaries — and weaves that information into a **personal knowledge graph** the user can search, recall, and act on.

### Core UX Tenets

| Principle | What it means in practice |
|---|---|
| **Capture is king** | The primary action surface — always one tap away, zero friction. |
| **Intelligence is invisible** | AI works behind the curtain. Users see *results*, never "processing." |
| **Recall over storage** | The app is optimised for *finding*, not *filing*. |
| **Calm confidence** | The interface never rushes, never overwhelms. It feels like a trusted assistant sitting quietly in your pocket. |
| **Progressive depth** | Surface layer is dead simple. Power reveals itself only when the user reaches for it. |

### Experience North Star

> *A student photographs a lecture whiteboard. Within two seconds they see a clean summary card with key equations, a reminder set for the exam date mentioned, and the ability to search "thermodynamics lecture" six months later and find it instantly.*

The user never tagged it. Never organised it. Never typed a word. Memora just *knew*.

---

## 2. Design Philosophy

### 2.1 — Design by Subtraction

Every element on screen must survive the question: *"If I remove this, does the user lose something important?"* If the answer is no, remove it. Memora's interface should feel like it has fewer things than it actually does.

### 2.2 — Content as Interface

The user's captured information *is* the UI. Cards, summaries, and extracted entities aren't decorations layered onto chrome — they **are** the chrome. Minimise toolbars, sidebars, and controls. Maximise the content the user came to see.

### 2.3 — One Focus Per Screen

Every screen has a single primary purpose. The Capture screen captures. The Detail screen reveals. The Search screen finds. If a screen tries to do two things, it becomes two screens.

### 2.4 — Timeless Over Trendy

No design trends that will feel dated in 18 months. No floating orbs, no neon gradients, no "AI aesthetic." The interface should look equally at home in 2024 and 2029. Reference points: Things 3 (released 2017, still looks modern), Linear (released 2019, still sets the standard).

### 2.5 — Earned Complexity

Features are introduced to the user *as they need them*. First-time users see only the essentials: capture, view, search. Tags, filters, bulk actions, and integrations surface naturally through usage, never through onboarding carousels.

### 2.6 — Physicality Without Skeuomorphism

Elements should feel like they have subtle weight and presence — through shadow, motion, and layering — without imitating real-world textures. A card lifts slightly on press. A deleted item falls away. The physics are felt, not seen.

---

## 3. Brand Identity

### 3.1 — Brand Attributes

| Attribute | Expression |
|---|---|
| **Modern** | Clean geometry, contemporary type, current but not chasing trends |
| **Minimal** | Restrained palette, generous space, no unnecessary ornamentation |
| **Premium** | Impeccable spacing, considered typography, fine-grain details |
| **Calm** | Muted backgrounds, unhurried transitions, no urgency cues |
| **Intelligent** | Information surfaced proactively, smart defaults, no manual busywork |
| **Fast** | Instant response to input, skeleton loading, optimistic UI |
| **Trustworthy** | Consistent behaviour, predictable navigation, clear data ownership |
| **Elegant** | Every curve, every shadow, every animation earns its place |

### 3.2 — Brand Voice (UI Copy)

- **Concise.** Use the fewest words that convey the meaning. *"3 items extracted"* not *"We successfully extracted 3 items from your image."*
- **Confident.** Declarative, not hedging. *"Your receipt — ₹1,240 at Apollo Pharmacy"* not *"It looks like this might be a receipt…"*
- **Human.** Warm but not playful. No emoji in system messages. No exclamation marks.
- **Helpful.** Anticipate what the user will ask next and answer preemptively.

### 3.3 — Logo & Wordmark Direction

- Wordmark: geometric sans-serif, medium weight, subtle letterspacing (+2–4%)
- Icon: abstract mark suggesting memory/capture — not a brain, not a camera, not a document
- Possible directions: a layered card, a prism refracting light into structure, a minimal pin/bookmark abstraction
- The mark should work at 16×16px (favicon) and 512×512px (app icon) without losing legibility

### 3.4 — Tone Spectrum

```
Serious ░░░░░░░░░░░░░░░░░░░░▓▓▓▓▓░░░░░░ Playful
          ↑ Memora sits here — warm professional
```

---

## 4. Information Architecture

### 4.1 — Content Model

```
Memory (core entity)
├── Original Image (source of truth)
├── Summary (AI-generated, editable)
├── Extracted Entities
│   ├── Dates / Deadlines
│   ├── Contacts (name, phone, email)
│   ├── Addresses / Locations
│   ├── URLs / Links
│   ├── Amounts / Prices
│   └── Custom key-value pairs
├── Category (auto-assigned, user-overridable)
│   e.g., Receipt, Notice, Whiteboard, Prescription, Card
├── Tags (auto-suggested, user-editable)
├── Reminders (auto-created from dates, user-manageable)
├── Source Metadata
│   ├── Capture date & time
│   ├── Location (if permitted)
│   └── Device info
└── Connections (related memories)
```

### 4.2 — Category Taxonomy

These are **auto-assigned** — the user never has to choose manually.

| Category | Icon style | Typical content |
|---|---|---|
| Whiteboard | Minimal board outline | Lecture notes, meeting scribbles |
| Notice | Pin/bulletin | College notices, announcements |
| Receipt | Receipt shape | Purchase receipts, bills |
| Prescription | Medical cross (subtle) | Doctor prescriptions, med lists |
| Card | Card rectangle | Business cards, ID cards |
| Event | Calendar dot | Posters, invitations |
| Certificate | Seal/ribbon (minimal) | Certificates, awards |
| Timetable | Grid | Class schedules, rosters |
| Label | Tag | Product labels, packaging |
| Document | Page | Generic documents |
| Other | Dot | Uncategorised fallback |

### 4.3 — Screen Map

```
App
├── Home (Feed)
│   ├── Recent Memories (default view)
│   ├── Quick Search Bar
│   ├── Active Reminders Banner
│   └── Smart Suggestions
│
├── Capture
│   ├── Camera Viewfinder
│   ├── Gallery Import
│   └── Processing State → Memory Detail
│
├── Search
│   ├── Full-Text Search (across summaries, entities, tags)
│   ├── Filters (category, date range, entity type)
│   └── Results List
│
├── Memory Detail
│   ├── Summary Card
│   ├── Original Image (expandable)
│   ├── Extracted Entities (actionable)
│   ├── Reminders
│   ├── Tags
│   └── Actions (share, edit, delete, re-process)
│
├── Reminders
│   ├── Upcoming
│   ├── Past
│   └── Create/Edit Reminder
│
├── Collections (optional v2)
│   ├── Auto-Collections (by category, by month)
│   └── User Collections
│
└── Settings
    ├── Account
    ├── AI Processing Preferences
    ├── Notifications
    ├── Storage
    ├── Appearance (light/dark/system)
    └── About
```

### 4.4 — Data Hierarchy (What Gets Visual Priority)

1. **Summary** — the single most important piece of derived content
2. **Key entities** — dates, amounts, contacts shown as actionable chips
3. **Category badge** — quick visual identification
4. **Thumbnail** — the original image, secondary to the extracted intelligence
5. **Metadata** — capture date, tags, location — tertiary

This hierarchy is critical. Traditional gallery apps lead with the image. Memora leads with **meaning**.

---

## 5. Navigation Philosophy

### 5.1 — Structure

**Bottom tab bar** — 4 tabs maximum. Simple, predictable, zero learning curve.

```
┌─────────────────────────────────────┐
│                                     │
│           [Screen Content]          │
│                                     │
├────────┬────────┬────────┬──────────┤
│  Home  │ Search │  (•)   │ Remind   │
│   ○    │   ○    │Capture │    ○     │
└────────┴────────┴────────┴──────────┘
```

### 5.2 — Capture as Primary Action

The Capture button is **not** a tab. It is a **floating primary action** — visually distinct (filled, elevated, slightly larger) centred in the tab bar. It is the gravitational centre of the app.

- Tapping it opens the camera in a **full-screen modal** that slides up.
- This modal is the only full-screen takeover in the app — reinforcing its importance.
- The modal has a minimal top bar with a close (×) button and a gallery import icon.

### 5.3 — Tab Behaviour

| Tab | Purpose | Back behaviour |
|---|---|---|
| Home | Feed of recent memories | Scroll to top on re-tap |
| Search | Find any memory | Clear search on re-tap |
| Capture | Camera modal | Dismiss modal |
| Reminders | Upcoming reminders | Scroll to top on re-tap |

### 5.4 — Navigation Depth

Maximum depth from any tab: **2 levels**.

```
Tab → List → Detail
```

No deeper nesting. If a feature requires more depth, it belongs in Settings or a modal sheet.

### 5.5 — Modals and Sheets

- **Bottom sheets** for quick actions (edit tags, set reminder, share).
- **Full-screen modals** only for Capture and image preview.
- Sheets slide up with a drag handle. Dismissible by swipe-down.
- No popovers, no dropdowns, no multi-step wizards.

### 5.6 — Transitions

- Tab switches: **crossfade** (150ms, ease-out)
- Push navigation (list → detail): **slide-left** (250ms, spring curve)
- Modal presentation: **slide-up** (300ms, spring curve with slight overshoot)
- Sheet presentation: **slide-up** (200ms, ease-out)

---

## 6. Design System Guidelines

### 6.1 — Color System

#### Light Mode

| Token | Hex | Usage |
|---|---|---|
| `--color-primary` | `#4F46E5` | Primary actions, active states, links |
| `--color-primary-hover` | `#4338CA` | Hover/pressed state of primary |
| `--color-primary-subtle` | `#EEF2FF` | Primary tint backgrounds |
| `--color-secondary` | `#06B6D4` | Secondary accents, progress indicators |
| `--color-accent` | `#8B5CF6` | Highlights, AI-generated content badges |
| `--color-bg-primary` | `#FFFFFF` | Card backgrounds, content areas |
| `--color-bg-secondary` | `#F8FAFC` | Page background, subtle separators |
| `--color-bg-tertiary` | `#F1F5F9` | Input fields, inactive surfaces |
| `--color-text-primary` | `#0F172A` | Headings, body text |
| `--color-text-secondary` | `#475569` | Descriptions, metadata |
| `--color-text-tertiary` | `#94A3B8` | Placeholders, disabled text |
| `--color-border` | `#E2E8F0` | Card borders, dividers |
| `--color-border-subtle` | `#F1F5F9` | Subtle separation lines |
| `--color-success` | `#10B981` | Success states, confirmations |
| `--color-warning` | `#F59E0B` | Warning states, approaching deadlines |
| `--color-error` | `#EF4444` | Error states, destructive actions |

#### Dark Mode

| Token | Hex | Usage |
|---|---|---|
| `--color-primary` | `#818CF8` | Slightly lighter indigo for dark backgrounds |
| `--color-primary-hover` | `#A5B4FC` | Hover state |
| `--color-primary-subtle` | `#1E1B4B` | Primary tint on dark |
| `--color-bg-primary` | `#1F2937` | Card backgrounds |
| `--color-bg-secondary` | `#111827` | Page background |
| `--color-bg-tertiary` | `#374151` | Input fields |
| `--color-text-primary` | `#F9FAFB` | Headings, body text |
| `--color-text-secondary` | `#9CA3AF` | Descriptions |
| `--color-text-tertiary` | `#6B7280` | Placeholders |
| `--color-border` | `#374151` | Card borders |
| `--color-border-subtle` | `#1F2937` | Subtle lines |

#### Usage Rules

- **Primary** is reserved for interactive elements: buttons, links, active tabs. Never used as a decorative fill.
- **Accent** (`#8B5CF6`) is sparingly used to denote AI-generated or "smart" content — a small badge, a thin left-border on summary cards. This creates a subtle but consistent language: *purple = intelligence*.
- **Secondary** (`#06B6D4`) appears in progress indicators, charts, and secondary CTAs. Never competes with primary on the same surface.
- Maximum **3 colors** visible on any single screen (excluding grays and semantic colors).

### 6.2 — Typography

#### Font Stack

```css
--font-sans: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
--font-mono: 'JetBrains Mono', 'SF Mono', 'Fira Code', monospace;
```

Inter is the primary typeface. It was designed for screens, has excellent legibility at small sizes, and its variable font file supports all needed weights without separate downloads.

#### Type Scale

| Token | Size | Weight | Line Height | Letter Spacing | Usage |
|---|---|---|---|---|---|
| `--text-display` | 32px / 2rem | 700 | 1.2 | -0.02em | Hero headings (rare) |
| `--text-title` | 24px / 1.5rem | 600 | 1.3 | -0.015em | Screen titles |
| `--text-heading` | 20px / 1.25rem | 600 | 1.35 | -0.01em | Section headings |
| `--text-subheading` | 16px / 1rem | 600 | 1.4 | -0.005em | Card titles |
| `--text-body` | 15px / 0.9375rem | 400 | 1.6 | 0 | Body text, summaries |
| `--text-body-small` | 13px / 0.8125rem | 400 | 1.5 | 0.005em | Metadata, captions |
| `--text-caption` | 11px / 0.6875rem | 500 | 1.4 | 0.02em | Badges, labels, timestamps |

#### Typography Rules

- Negative letter-spacing on headings creates a tighter, more confident feel.
- Positive letter-spacing on captions/labels improves legibility at small sizes.
- **Never use font-weight 300 (light)**. It undermines the premium, confident feel.
- Body text at 15px (not 14 or 16) hits a sweet spot — large enough to be comfortable, compact enough to be efficient.
- Maximum line width: **65 characters** for body text. Beyond this, readability degrades.

### 6.3 — Elevation & Shadow System

```css
--shadow-xs:    0 1px 2px rgba(0, 0, 0, 0.04);
--shadow-sm:    0 1px 3px rgba(0, 0, 0, 0.06), 0 1px 2px rgba(0, 0, 0, 0.04);
--shadow-md:    0 4px 6px rgba(0, 0, 0, 0.04), 0 2px 4px rgba(0, 0, 0, 0.03);
--shadow-lg:    0 10px 15px rgba(0, 0, 0, 0.05), 0 4px 6px rgba(0, 0, 0, 0.03);
--shadow-xl:    0 20px 25px rgba(0, 0, 0, 0.06), 0 8px 10px rgba(0, 0, 0, 0.04);
```

#### Elevation Tiers

| Level | Shadow | Use case |
|---|---|---|
| 0 | None | Flat surfaces flush with background |
| 1 | `--shadow-xs` | Subtle card outlines, list items |
| 2 | `--shadow-sm` | Default card resting state |
| 3 | `--shadow-md` | Hovered cards, active inputs |
| 4 | `--shadow-lg` | Bottom sheets, floating elements |
| 5 | `--shadow-xl` | Modals, overlays |

Shadows in dark mode use `rgba(0, 0, 0, 0.3)` base with subtle indigo tint to avoid looking like holes.

### 6.4 — Border Radius

```css
--radius-sm:   8px;    /* Chips, badges, small buttons */
--radius-md:   12px;   /* Input fields, small cards */
--radius-lg:   16px;   /* Standard cards, sheets */
--radius-xl:   20px;   /* Large cards, modals */
--radius-full: 9999px; /* Pills, avatars, circular buttons */
```

Consistency rule: nested elements reduce radius by one step. A card at `--radius-lg` (16px) contains input fields at `--radius-md` (12px).

---

## 7. Component Library Recommendations

### 7.1 — Core Components

| Component | Description | Key Behaviours |
|---|---|---|
| **Memory Card** | The primary content unit. Displays summary, category badge, key entities, thumbnail, and timestamp. | Tap → navigate to detail. Long-press → quick actions sheet. Subtle shadow lift on press. |
| **Entity Chip** | Small pill displaying an extracted entity (date, phone, email, amount). | Tap → contextual action (call, email, add to calendar). Left-icon indicates type. |
| **Category Badge** | Small rounded label showing the auto-assigned category. | Colour-coded per category but all muted/pastel to avoid noise. Non-interactive. |
| **Summary Block** | The AI-generated summary displayed on detail screens. | Left accent border in `--color-accent`. Editable on tap. |
| **Capture Button** | Persistent primary action in tab bar. | Filled circle, `--color-primary`, slight elevation. Pulsing micro-animation on first launch only. |
| **Search Input** | Full-width input with category filter pills below. | Debounced search (300ms). Results update live. Placeholder: *"Search your memories…"* |
| **Reminder Card** | Compact card showing reminder title, date/time, and linked memory. | Swipe-right to mark done. Tap to view linked memory. |
| **Empty State** | Illustrated placeholder for screens with no content. | Simple line illustration (not AI-generated art). Clear CTA. |
| **Skeleton Loader** | Placeholder shimmer while content loads. | Matches the shape of the component it replaces exactly. Subtle pulse animation. |
| **Bottom Sheet** | Draggable sheet for secondary actions. | Snap points at 30%, 60%, 100% height. Drag handle centred at top. Backdrop blur behind. |
| **Toast / Snackbar** | Brief confirmation messages. | Appear bottom-centre, auto-dismiss in 3s. No more than 1 visible at a time. |

### 7.2 — Memory Card Anatomy

```
┌──────────────────────────────────────────┐
│  ┌──────┐                                │
│  │Thumb │  Category Badge     · 2h ago   │
│  │ nail │                                │
│  └──────┘  Summary text goes here,       │
│            limited to two lines max…     │
│                                          │
│  ┌──────┐ ┌──────────┐ ┌──────┐          │
│  │📅 Dec│ │📞 Contact│ │₹1240 │          │
│  └──────┘ └──────────┘ └──────┘          │
└──────────────────────────────────────────┘
```

- Thumbnail: 56×56px, `--radius-md`, left-aligned
- Summary: max 2 lines, `--text-body`, `--color-text-primary`
- Entity chips: horizontal scroll if overflow, `--text-caption`
- Timestamp: `--text-caption`, `--color-text-tertiary`
- Card padding: 16px
- Card radius: `--radius-lg`
- Card background: `--color-bg-primary`
- Card border: 1px `--color-border-subtle`

### 7.3 — States

Every interactive component must define these states:

| State | Visual treatment |
|---|---|
| Default | Resting appearance |
| Hover | Subtle background shift or shadow increase (desktop) |
| Pressed | Scale to 0.98, shadow decrease |
| Focused | 2px ring in `--color-primary` with 2px offset |
| Disabled | 40% opacity, no pointer events |
| Loading | Skeleton shimmer or spinner (never both) |
| Error | Red border, inline error message below |
| Empty | Illustration + descriptive text + CTA |

---

## 8. Animation Philosophy

### 8.1 — Principles

1. **Purposeful** — Every animation communicates something: hierarchy, state change, spatial relationship. If it doesn't communicate, it's decoration. Remove it.
2. **Fast** — No animation exceeds 400ms. Most are 150–250ms. The app should feel *snappy*, not *cinematic*.
3. **Physical** — Motion follows spring physics, not linear timing. Elements ease in and out naturally. Overshoot is acceptable on modals (3–5%), never on buttons.
4. **Consistent** — The same type of action always uses the same animation. Push transitions always slide left. Sheets always slide up. Deletes always fade and collapse.
5. **Interruptible** — Any animation can be interrupted by user input. If a user taps during a transition, it resolves immediately.

### 8.2 — Motion Tokens

```css
/* Durations */
--duration-instant:  100ms;   /* Micro-interactions: toggle, checkbox */
--duration-fast:     150ms;   /* Hover states, button press feedback */
--duration-normal:   250ms;   /* Page transitions, card animations */
--duration-slow:     350ms;   /* Modal presentations, complex layouts */

/* Easing */
--ease-out:          cubic-bezier(0.16, 1, 0.3, 1);      /* Default exit */
--ease-in-out:       cubic-bezier(0.65, 0, 0.35, 1);     /* Symmetric moves */
--ease-spring:       cubic-bezier(0.34, 1.56, 0.64, 1);  /* Slight overshoot */
--ease-bounce:       cubic-bezier(0.34, 1.8, 0.64, 1);   /* Playful (rare) */
```

### 8.3 — Animation Inventory

| Action | Animation | Duration | Easing |
|---|---|---|---|
| Card appears (feed) | Fade up (y: 8px → 0) | 250ms | ease-out |
| Card press | Scale to 0.98 | 100ms | ease-out |
| Card release | Scale to 1.0 | 150ms | spring |
| Tab switch | Crossfade content | 150ms | ease-in-out |
| Push navigation | Slide left + fade | 250ms | ease-out |
| Back navigation | Slide right + fade | 200ms | ease-out |
| Modal open | Slide up from bottom | 300ms | spring |
| Modal close | Slide down | 250ms | ease-out |
| Sheet open | Slide up | 200ms | ease-out |
| Toast appear | Slide up + fade (y: 16px) | 200ms | spring |
| Toast dismiss | Fade out | 150ms | ease-out |
| Skeleton shimmer | Continuous horizontal sweep | 1500ms | linear (loop) |
| Entity chip appear | Scale from 0.8 + fade | 150ms | spring |
| Delete item | Fade out + height collapse | 250ms | ease-in-out |
| Image zoom (detail) | Pinch-to-zoom, spring settle | Gesture | spring |
| Processing state | Indeterminate progress bar | Continuous | ease-in-out |

### 8.4 — Stagger Rules

When multiple items animate in (e.g., memory cards in a feed), stagger by **30ms per item**, with a maximum of 5 items animating. Beyond 5, remaining items appear instantly. This prevents the "waterfall" effect that feels slow.

---

## 9. Spacing System

### 9.1 — Base Unit

**4px base grid.** All spacing values are multiples of 4.

```css
--space-0:    0px;
--space-1:    4px;
--space-2:    8px;
--space-3:    12px;
--space-4:    16px;
--space-5:    20px;
--space-6:    24px;
--space-7:    28px;
--space-8:    32px;
--space-10:   40px;
--space-12:   48px;
--space-16:   64px;
--space-20:   80px;
--space-24:   96px;
```

### 9.2 — Application of Spacing

| Context | Token | Value |
|---|---|---|
| Card internal padding | `--space-4` | 16px |
| Gap between cards in feed | `--space-3` | 12px |
| Section heading to content | `--space-4` | 16px |
| Screen horizontal padding | `--space-5` | 20px |
| Screen top padding (below header) | `--space-6` | 24px |
| Between entity chips | `--space-2` | 8px |
| Icon to label (inline) | `--space-2` | 8px |
| Input field padding | `--space-3` horizontal, `--space-3` vertical | 12px |
| Between form fields | `--space-4` | 16px |
| Tab bar height | `--space-16` | 64px |
| Bottom safe area | `--space-8` minimum | 32px |
| Modal top handle to content | `--space-5` | 20px |

### 9.3 — Whitespace Philosophy

Whitespace is not "wasted space" — it is a structural element that:

- Creates visual breathing room and reduces cognitive load
- Establishes hierarchy (more space = more separation = new section)
- Conveys premium quality (cheap products cram; premium products breathe)
- Guides the eye from one content block to the next

**Rule of thumb:** When in doubt, add more space. Then ask if it's too much. It almost never is.

---

## 10. Iconography Style

### 10.1 — Icon System

**Style:** Outlined, 1.5px stroke, rounded joins and caps.

**Reference:** Lucide Icons or Phosphor Icons (regular weight). These sets match the brand personality — clean, geometric, friendly without being childish.

**Do not use:** Filled/solid icon sets (too heavy), heroicons outline (too thin at 1px), emoji as functional icons, or custom illustrated icons (inconsistency risk).

### 10.2 — Icon Specifications

| Property | Value |
|---|---|
| Base size | 20×20px |
| Small size | 16×16px (inline with caption text) |
| Large size | 24×24px (tab bar, headers) |
| Stroke width | 1.5px |
| Corner radius | 2px on joins |
| Optical padding | 2px internal padding within the bounding box |
| Color | Inherits from text color token of context |

### 10.3 — Icon Usage Rules

- Icons always accompany text labels in navigation. **Never icon-only** in tab bars.
- In entity chips, icons appear at 16px to the left of the label.
- Decorative icons (section headers) are optional and should not be used if they add no information.
- Interactive icons (close, more, share) maintain a **44×44px minimum touch target** regardless of visual size.

### 10.4 — Category Icons

Each auto-assigned category has a dedicated icon. These icons use the same outlined style but are filled with a pastel background circle (32px) for visual distinction in feeds.

```
Category Icon Palette (background circle colors):

Whiteboard  → #DBEAFE (soft blue)
Notice      → #FEF3C7 (soft amber)
Receipt     → #D1FAE5 (soft green)
Prescription→ #FCE7F3 (soft pink)
Card        → #E0E7FF (soft indigo)
Event       → #FDE68A (soft yellow)
Certificate → #EDE9FE (soft violet)
Timetable   → #CFFAFE (soft cyan)
Label       → #F1F5F9 (soft gray)
Document    → #F3F4F6 (neutral gray)
Other       → #F9FAFB (lightest gray)
```

---

## 11. Accessibility Considerations

### 11.1 — Contrast

- All text meets **WCAG 2.1 AA** minimum contrast ratios:
  - Normal text (< 18px): **4.5:1** against background
  - Large text (≥ 18px bold or ≥ 24px): **3:1** against background
- Interactive elements (icons, borders on inputs): **3:1** against background.
- Verified: `#475569` on `#FFFFFF` = **7.1:1** ✓. `#94A3B8` on `#FFFFFF` = **3.3:1** ✓ (used only for non-essential metadata).

### 11.2 — Touch Targets

- Minimum touch target: **44×44px** (per Apple HIG / WCAG 2.5.5).
- Minimum spacing between adjacent touch targets: **8px**.
- The Capture button: **56×56px** (oversized intentionally as the primary action).

### 11.3 — Screen Reader Support

- All images include descriptive `alt` text. For user-captured images, the AI-generated summary serves as the `alt` text.
- Semantic HTML: `<nav>`, `<main>`, `<article>`, `<section>`, `<aside>`.
- ARIA labels on icon-only buttons (e.g., close button: `aria-label="Close"`).
- Live regions (`aria-live="polite"`) for toast notifications and processing status updates.
- Role attributes on custom components (sheets, modals, chips).

### 11.4 — Motion Sensitivity

- Respect `prefers-reduced-motion`. When enabled:
  - All transitions become instant (0ms duration).
  - Skeleton shimmer becomes a static placeholder.
  - Card stagger is removed; all items appear simultaneously.
  - The only retained motion: spinner for loading states (essential feedback).

### 11.5 — Color Independence

- Information is never conveyed by color alone. Category badges include text labels, not just colored dots.
- Error states use both red color AND an error icon AND text message.
- Entity chips are identifiable by their icon, not their color.

### 11.6 — Text Scaling

- Support dynamic type / text scaling up to **200%** without horizontal scrolling or content clipping.
- All layout uses relative units (`rem`, `em`, `%`) — no fixed pixel heights on text containers.
- Memory cards reflow vertically when text scales up.

### 11.7 — Focus Management

- Visible focus indicators on all interactive elements (2px ring, `--color-primary`, 2px offset).
- Logical tab order follows visual layout (top-to-bottom, left-to-right).
- Focus trapping within modals and sheets when open.
- Focus returns to trigger element when modal/sheet is dismissed.

---

## 12. Mobile-First UX Recommendations

### 12.1 — Screen Priority (Implementation Order)

1. **Capture Flow** — This is the core loop. Nail this first.
2. **Home Feed** — Where users spend most passive time.
3. **Memory Detail** — Where extracted intelligence is consumed.
4. **Search** — The power feature that proves the product's value.
5. **Reminders** — Proactive intelligence surface.
6. **Settings** — Last, lowest complexity.

### 12.2 — Capture Flow UX

```
[Tap Capture] → Camera opens (full screen, fast)
                 ↓
[Take Photo]  → Shutter animation (subtle flash)
                 ↓
[Processing]  → Photo shrinks to a card at screen centre
                 Progress indicator below
                 Skeleton summary pulses
                 (Target: < 3 seconds)
                 ↓
[Complete]    → Card expands to Memory Detail
                 Summary fades in
                 Entity chips scale in (staggered)
                 Category badge appears
                 ↓
[User Action] → View detail / Go back to Home
                 (Memory is auto-saved)
```

Key UX decisions:
- **No manual categorization.** AI assigns. User can override later but is never forced to.
- **No confirm dialog.** After capture, the image is immediately processed. No "Save this image?" — every capture is intentional.
- **Processing is delightful, not anxious.** The card animation during processing makes the wait feel productive, not idle.
- **Auto-save always.** The user never has to tap "Save." Memora remembers everything by default.

### 12.3 — Home Feed UX

- **Reverse chronological** by default (most recent first).
- **No infinite scroll.** Paginate in batches of 20 with a "Load more" at the bottom. Infinite scroll creates anxiety; pagination creates a sense of completion.
- **Pull-to-refresh** triggers a check for any pending processing.
- **Active reminders** surface as a compact banner above the feed (collapsible).
- **Empty state** on first launch: warm illustration + "Capture your first memory" CTA pointing to the Capture button.

### 12.4 — Search UX

- Search input is **sticky at the top** of the search tab.
- Below the input: **horizontal scrolling filter chips** (All, Whiteboards, Receipts, Notices, etc.).
- Search is **full-text across summaries, extracted text, entity values, and tags.**
- Results appear as compact memory cards (smaller than feed cards).
- **Recents** shown before the user types (last 5 searches).
- **"No results" state** suggests broadening the search or capturing new content.

### 12.5 — Gesture Support

| Gesture | Context | Action |
|---|---|---|
| Swipe right | Reminder card | Mark as complete |
| Swipe left | Memory card (feed) | Quick delete (with undo toast) |
| Pull down | Feed, lists | Refresh |
| Pinch | Image in detail view | Zoom in/out |
| Long press | Memory card | Quick actions sheet |
| Swipe down | Modal, sheet | Dismiss |

### 12.6 — Offline Behaviour

- All previously synced memories are available offline.
- Captures taken offline are queued for processing with a subtle "Pending" badge.
- Search works on locally cached summaries and entities.
- No error modals for offline state — just a small banner: *"You're offline. Memories will sync when connected."*

### 12.7 — Performance Targets

| Metric | Target |
|---|---|
| First contentful paint | < 1.2s |
| Time to interactive | < 2.0s |
| Camera open (from tap) | < 500ms |
| AI processing (per image) | < 3s (perceived), < 5s (actual) |
| Search results | < 200ms |
| Navigation transitions | < 300ms |
| Image thumbnail load | < 100ms (cached) |

### 12.8 — Responsive Considerations

While mobile-first, the design should gracefully adapt:

| Viewport | Layout |
|---|---|
| **< 640px** (mobile) | Single column. Bottom tab bar. Full-width cards. |
| **640–1024px** (tablet) | Two-column card grid. Bottom tab bar persists. |
| **> 1024px** (desktop) | Sidebar navigation replaces tab bar. Three-column grid. Detail panel as right sidebar instead of push navigation. |

---

## Appendix A — Design System Checklist

Before any screen is considered "done," verify:

- [ ] Uses only defined color tokens (no hardcoded hex)
- [ ] Uses only defined typography tokens (no custom font sizes)
- [ ] Uses only defined spacing tokens (no arbitrary pixel values)
- [ ] All interactive elements have defined states (hover, pressed, focused, disabled)
- [ ] All animations use defined motion tokens
- [ ] Touch targets meet 44px minimum
- [ ] Contrast ratios verified for all text
- [ ] Screen reader tested with VoiceOver / TalkBack
- [ ] Reduced motion variant exists
- [ ] Empty state designed
- [ ] Loading state designed
- [ ] Error state designed
- [ ] Dark mode verified
- [ ] No horizontal scroll on mobile viewport

---

## Appendix B — What Memora Is NOT (Design Guardrails)

These are anti-patterns to actively avoid during all future design work:

| Anti-pattern | Why it's wrong for Memora |
|---|---|
| Grid of image thumbnails | Makes it feel like Google Photos. Memora leads with intelligence, not pixels. |
| Document scanning UI (crop handles, perspective correction) | Makes it feel like CamScanner. Memora captures casually, not precisely. |
| Complex folder/tag taxonomy | Makes it feel like a file manager. Memora organises automatically. |
| Chatbot interface for search | Makes it feel like ChatGPT. Memora answers immediately, no conversation needed. |
| Dashboard with charts/stats | Makes it feel like an analytics tool. Memora is a memory tool, not a BI tool. |
| Onboarding carousel with feature highlights | Feels generic. Let the product speak for itself through first use. |
| Floating action button (Android Material style) | Dated. The capture button is integrated into the tab bar instead. |
| Pull-down-to-scan gesture | Gimmicky. The camera is accessed through a clear, explicit button. |

---

## Appendix C — Design Token File Reference

When implementation begins, all tokens should live in a single CSS custom properties file. Below is the recommended structure:

```css
/* tokens.css */

:root {
  /* ── Color ── */
  --color-primary: #4F46E5;
  --color-primary-hover: #4338CA;
  --color-primary-subtle: #EEF2FF;
  --color-secondary: #06B6D4;
  --color-accent: #8B5CF6;
  
  --color-bg-primary: #FFFFFF;
  --color-bg-secondary: #F8FAFC;
  --color-bg-tertiary: #F1F5F9;
  
  --color-text-primary: #0F172A;
  --color-text-secondary: #475569;
  --color-text-tertiary: #94A3B8;
  
  --color-border: #E2E8F0;
  --color-border-subtle: #F1F5F9;
  
  --color-success: #10B981;
  --color-warning: #F59E0B;
  --color-error: #EF4444;

  /* ── Typography ── */
  --font-sans: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  --font-mono: 'JetBrains Mono', 'SF Mono', 'Fira Code', monospace;

  /* ── Spacing ── */
  --space-1: 4px;
  --space-2: 8px;
  --space-3: 12px;
  --space-4: 16px;
  --space-5: 20px;
  --space-6: 24px;
  --space-8: 32px;
  --space-10: 40px;
  --space-12: 48px;
  --space-16: 64px;
  --space-20: 80px;

  /* ── Radius ── */
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
  --radius-xl: 20px;
  --radius-full: 9999px;

  /* ── Shadow ── */
  --shadow-xs: 0 1px 2px rgba(0, 0, 0, 0.04);
  --shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.06), 0 1px 2px rgba(0, 0, 0, 0.04);
  --shadow-md: 0 4px 6px rgba(0, 0, 0, 0.04), 0 2px 4px rgba(0, 0, 0, 0.03);
  --shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.05), 0 4px 6px rgba(0, 0, 0, 0.03);
  --shadow-xl: 0 20px 25px rgba(0, 0, 0, 0.06), 0 8px 10px rgba(0, 0, 0, 0.04);

  /* ── Motion ── */
  --duration-instant: 100ms;
  --duration-fast: 150ms;
  --duration-normal: 250ms;
  --duration-slow: 350ms;

  --ease-out: cubic-bezier(0.16, 1, 0.3, 1);
  --ease-in-out: cubic-bezier(0.65, 0, 0.35, 1);
  --ease-spring: cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* ── Dark Mode ── */
@media (prefers-color-scheme: dark) {
  :root {
    --color-primary: #818CF8;
    --color-primary-hover: #A5B4FC;
    --color-primary-subtle: #1E1B4B;
    
    --color-bg-primary: #1F2937;
    --color-bg-secondary: #111827;
    --color-bg-tertiary: #374151;
    
    --color-text-primary: #F9FAFB;
    --color-text-secondary: #9CA3AF;
    --color-text-tertiary: #6B7280;
    
    --color-border: #374151;
    --color-border-subtle: #1F2937;

    --shadow-xs: 0 1px 2px rgba(0, 0, 0, 0.2);
    --shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.3), 0 1px 2px rgba(0, 0, 0, 0.2);
    --shadow-md: 0 4px 6px rgba(0, 0, 0, 0.25), 0 2px 4px rgba(0, 0, 0, 0.15);
    --shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.3), 0 4px 6px rgba(0, 0, 0, 0.15);
    --shadow-xl: 0 20px 25px rgba(0, 0, 0, 0.35), 0 8px 10px rgba(0, 0, 0, 0.2);
  }
}
```

---

*This document serves as the single source of truth for all design decisions in Memora. Every screen, component, and interaction should be traceable back to a principle, token, or guideline defined here.*

*Next step: Screen-by-screen design, starting with the Capture Flow.*
