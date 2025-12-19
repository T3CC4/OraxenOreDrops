# OraxenOreDrops

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21+-green.svg)](https://www.minecraft.net/)
[![Paper](https://img.shields.io/badge/paper-required-red.svg)](https://papermc.io/)

Advanced custom ore drops plugin for Minecraft servers using [Oraxen](https://www.spigotmc.org/resources/oraxen.72448/). Features intelligent Fortune/Looting scaling with three different drop mechanics to ensure balanced gameplay.

## ✨ Features

- 🎯 **Custom Block Drops** - Configure any block to drop custom Oraxen items
- 📊 **Smart Fortune Scaling** - Three different methods to balance rare drops
- 🔧 **VeinMiner Support** - Automatic detection and reduced drops during VeinMiner usage
- 💎 **AdvancedEnchantments Integration** - Support for custom Fortune/Looting enchantments
- 🚫 **Silk Touch Prevention** - No custom drops when using Silk Touch
- ⚡ **Performance Optimized** - Async cleanup tasks and efficient caching
- 🐛 **Debug Mode** - Detailed logging for testing drop rates

## 📋 Requirements

- **Server**: Paper/Purpur/Pufferfish 1.21+
- **Dependencies**:
    - [Oraxen](https://www.spigotmc.org/resources/oraxen.72448/) (required)
    - [AdvancedEnchantments](https://www.spigotmc.org/resources/advancedenchantments.43058/) (optional)
- **Java**: 21+

## 📥 Installation

1. Download the latest release from [Modrinth](https://modrinth.com/)
2. Place the JAR file in your `plugins/` folder
3. Install Oraxen if not already installed
4. Restart your server
5. Configure drops in `plugins/OraxenOreDrops/config.yml`

## ⚙️ Configuration

### Basic Example

```yaml
debug-mode: false

drop-mechanics:
  method: HYBRID  # DIMINISHING, BONUS_ROLLS, or HYBRID

block-drops:
  DIAMOND_ORE:
    drop1:
      oraxen-item: "diamond_shard"
      chance: 25.0        # 25% base chance
      min-amount: 1
      max-amount: 3
    drop2:
      oraxen-item: "rare_gem"
      chance: 0.5         # 0.5% rare drop
      min-amount: 1
      max-amount: 1
```

### Drop Mechanics Methods

#### HYBRID (Recommended)
Balances both chance increase and bonus rolls based on rarity:
- **Common drops (>50%)**: Mainly chance increase
- **Rare drops (<1%)**: Mainly bonus rolls
- Best overall balance for mixed drop tables

#### DIMINISHING
Uses logarithmic scaling for rare items:
- Fortune I on 0.1% drop: ~0.13% (+0.03%)
- Fortune III on 0.1% drop: ~0.117% (+0.017%)
- Best for ultra-rare drops that should stay rare

#### BONUS_ROLLS
Additional drop attempts instead of increasing chance:
- Fortune I on 0.1% drop: 2 rolls = ~0.2%
- Fortune III on 0.1% drop: 4 rolls = ~0.4%
- Most predictable and balanced

### Drop Chance Guidelines

| Rarity | Chance Range | Description | Fortune Bonus |
|--------|-------------|-------------|---------------|
| Common | 50-100% | Frequent drops | Full (+1% per level) |
| Uncommon | 10-50% | Regular drops | Reduced (+0.5% per level) |
| Rare | 1-10% | Occasional drops | Logarithmic |
| Very Rare | 0.1-1% | Seldom drops | Square root |
| Legendary | <0.1% | Extremely rare | Minimal |

## 🎮 Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/oraxenoredrops reload` | `oraxenoredrops.reload` | Reload configuration |
| `/oraxenoredrops debug <on\|off>` | `oraxenoredrops.debug` | Toggle debug mode |
| `/oraxenoredrops info` | `oraxenoredrops.info` | Show plugin info |

## 🔧 Permissions

| Permission | Default | Description |
|-----------|---------|-------------|
| `oraxenoredrops.use` | op | Use plugin commands |
| `oraxenoredrops.reload` | op | Reload config |
| `oraxenoredrops.debug` | op | Toggle debug mode |
| `oraxenoredrops.info` | true | View plugin info |

## 🎯 Features in Detail

### VeinMiner Detection
Automatically detects VeinMiner usage and reduces Fortune level by 5 to prevent excessive drops. Only processes every 5th block in a vein mine session.

### AdvancedEnchantments Support
Seamlessly integrates with AdvancedEnchantments custom enchantments:
- Custom Fortune/Luck enchantments
- Custom Looting enchantments
- Custom Silk Touch enchantments

### Smart Fortune Scaling
Drop chances scale intelligently based on rarity:
- Common items get full Fortune bonus
- Rare items get reduced Fortune bonus
- Ultra-rare items stay rare even with Fortune X

### Example Calculations

**Common Drop (50% chance)**
- Fortune 0: 50%
- Fortune III: 53%
- Fortune X: 60%

**Rare Drop (1% chance)**
- Fortune 0: 1%
- Fortune III: 1.12%
- Fortune X: 1.35%

**Ultra-Rare Drop (0.1% chance)**
- Fortune 0: 0.1%
- Fortune III: 0.117%
- Fortune X: 0.133%

## 🏗️ Building from Source

```bash
git clone https://github.com/yourusername/OraxenOreDrops.git
cd OraxenOreDrops
mvn clean package
```

The compiled JAR will be in `target/OraxenOreDrops-1.0.jar`

## 🐛 Bug Reports & Feature Requests

Please use the [GitHub Issues](https://github.com/yourusername/OraxenOreDrops/issues) page to report bugs or request features.

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 💖 Credits

- **Oraxen** by [Th0rgal](https://github.com/Th0rgal)
- **AdvancedEnchantments** by [N1KN](https://www.spigotmc.org/resources/advancedenchantments.43058/)

---

Made with ❤️ for the Minecraft community