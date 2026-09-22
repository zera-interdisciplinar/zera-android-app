# Invitation Sign-Up Hardening Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make employee invite redeem + auto-login production-ready: serializable DTOs, consistent HTTP errors, SignUp UI bound to ViewModel state, and an empty auth back stack after success.

**Architecture:** Keep the existing layers (DTO → `InvitationService` → `InvitationUseCase` → `InvitationViewModel` → `SingUpScreen`). Align redeem with `SingIn`: Retrofit returns the body (HTTP errors throw), ViewModel uses `try/catch`, then `SingIn.execute` to persist tokens. Add `PushAndClear` so Home replaces Splash/Welcome/Login/Register.

**Tech Stack:** Kotlin, Jetpack Compose, Retrofit, kotlinx.serialization, Navigation Compose.

## Global Constraints

- Do not rename `SingIn`/`SingUp` in this pass (pre-existing typo).
- Invite code UI stays `ZeraTokenInput` (6 digits).
- Portuguese user-facing copy.
- Do not commit unless the user asks.
- Do not add test libraries; no new unit tests that would need Mockito.

---

### Task 1: Serializable DTOs and a single redeem API

**Files:**
- Modify: `app/src/main/java/com/zera/android/model/entity/invitation/RedeemRequestDTO.kt`
- Modify: `app/src/main/java/com/zera/android/model/entity/invitation/RedeemResponseDTO.kt`
- Modify: `app/src/main/java/com/zera/android/model/remote/service/InvitationService.kt`
- Modify: `app/src/main/java/com/zera/android/model/remote/service/AuthService.kt`

- [ ] Add `@Serializable` to both redeem DTOs (same pattern as `SingInRequestDTO`).
- [ ] Change `InvitationService.redeem` to return `RedeemResponseDTO` (not `Response<T>`). Remove unused `Path` import.
- [ ] Remove `AuthService.signUp` so only `InvitationService` posts `invitations/redeem`.

---

### Task 2: Use case throws; ViewModel handles errors and auto-login

**Files:**
- Modify: `app/src/main/java/com/zera/android/model/usecase/auth/InvitationUseCase.kt`
- Modify: `app/src/main/java/com/zera/android/viewmodel/auth/Invitation.kt`

- [ ] `execute` returns `RedeemResponseDTO` (non-null), same call style as `SingIn`.
- [ ] Validate blank fields, email (`@` + `.`), token length 6, then `try/catch`.
- [ ] On redeem success, call injected/`private val` `SingIn`. If login fails, `pushAndPop(Route.Login)` and keep a PT error (account may already exist).
- [ ] On role success, `pushAndClear` Home. Always set `isLoading = false` except when leaving the screen on success.
- [ ] Portuguese `errorMessage`s. `private val useCase`. Drop unused imports.

---

### Task 3: Clear unauthenticated back stack

**Files:**
- Modify: `app/src/main/java/com/zera/android/view/navigation/NavCommand.kt`
- Modify: `app/src/main/java/com/zera/android/view/navigation/ZeraNavigator.kt`
- Modify: `app/src/main/java/com/zera/android/view/navigation/ZeraNavHost.kt`
- Modify: `app/src/main/java/com/zera/android/viewmodel/auth/SingIn.kt`

- [ ] Add `NavCommand.PushAndClear(route)` and `ZeraNavigator.pushAndClear`.
- [ ] In `ZeraNavHost`, navigate then `popUpTo(navController.graph.id) { inclusive = true }`.
- [ ] Use `pushAndClear` for ManagerHome/EmployeeHome in both ViewModels.

---

### Task 4: SignUp UI wiring

**Files:**
- Modify: `app/src/main/java/com/zera/android/view/screens/auth/SingUpScreen.kt`

- [ ] Show red `CaptionText` for `errorMessage`.
- [ ] Button `"Cadastrar"` / `"Cadastrando..."`, `enabled = !state.isLoading`.
- [ ] `onFilled` → `viewModel.redeem()`.
- [ ] Caption “Já tenho conta” → `ZeraNavigator.push(Route.Login)`.
- [ ] Preview uses default `viewModel()`. Remove unused saveable imports.

---

### Verification

- Compile the app module.
- Manual: empty submit, short code, happy path, HTTP failure, back after home (should not return to Welcome/Register).
