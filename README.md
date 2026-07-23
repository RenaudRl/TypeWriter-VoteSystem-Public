# VoteSystem Extension

![Java Version](https://img.shields.io/badge/Java-21-orange)
![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Target](https://img.shields.io/badge/Target-Paper%20/%20Folia%20/%20BTC--CORE-blue)

**VoteSystem Extension** is a voting management module for **TypeWriter**, engineered for **BTC Studio** infrastructure. It enables comprehensive voting mechanics with cooldown management and reward triggers.

---

## 🚀 Key Features

### 🗳️ Voting Mechanics
- **Cooldown triggers**: Execute actions automatically when vote cooldowns expire.
- **Progress Tracking**: Track player voting habits and history.

### 📋 Menu Integration
- **Dynamic Interfaces**: Organized menus to view active votes and cooldown statuses.
- **Category Support**: Group votes logically for better user experience.

---

## ⚙️ Configuration

VoteSystem Extension configuration is managed via TypeWriter's manifest system.

## 🛠 Building & Deployment

Requires **Java 21**.

```bash
# Clone the repository
git clone https://github.com/RenaudRl/TypeWriter-VoteSystem-Public.git
cd TypeWriter-VoteSystem-Public

# Build the project
./gradlew clean build
```

### Artifact Locations:
- `build/libs/VoteSystem-[Version].jar`

---

## 🤝 Credits & Inspiration
- **[TypeWriter](https://github.com/gabber235/Typewriter)** - The engine this extension is built for.
- **[BTC Studio](https://github.com/RenaudRl)** - Maintenance and specialized optimizations.

---

## 📜 License
Licensed under the **MIT License**.

## Documentation

Full documentation available at [BTC Studio Docs](https://docs.borntocraftstudio.net/extensions/free/vote-system/).
