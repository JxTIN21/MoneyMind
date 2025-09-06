# 💰 MoneyMind - Personal Finance Tracker

[![Java](https://img.shields.io/badge/Java-17+-brightgreen.svg)](https://www.java.com/)
[![SQLite](https://img.shields.io/badge/Database-SQLite-blue.svg)](https://www.sqlite.org/)
[![Swing](https://img.shields.io/badge/GUI-Java%20Swing-orange.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A comprehensive **Java Swing-based Personal Finance Management System** that demonstrates advanced data structures, algorithms, and database integration. Built as a learning project showcasing enterprise-level software development practices.

## 🌟 Features

### 💳 **Transaction Management**
- Complete CRUD operations for financial transactions
- Advanced search and filtering capabilities
- Multiple sorting algorithms (Merge Sort, Quick Sort, Binary Search)
- Real-time transaction validation and categorization
- Import/Export functionality (CSV support)

### 📊 **Budget Planning & Tracking**
- Create monthly and yearly budgets
- Real-time budget utilization tracking
- Smart budget alerts and notifications
- Budget performance analysis and scoring
- Predictive spending forecasts

### 🗂️ **Category Management**
- Hierarchical category structure with tree visualization
- Binary Search Tree implementation for efficient operations
- Drag-and-drop category reorganization
- Category health checks and validation
- Income vs Expense categorization

### 📈 **Financial Reports & Analytics**
- Comprehensive financial summaries
- Trend analysis with monthly/yearly views
- Category-wise expense breakdown
- Financial health scoring algorithm
- Interactive charts and visualizations

### 🎯 **Advanced Features**
- **AI-like Financial Health Assessment** - Intelligent scoring based on spending patterns
- **Multi-criteria Data Analysis** - Complex financial insights and recommendations
- **Professional UI/UX** - Tabbed interface with custom renderers and themes
- **Database Integration** - SQLite with proper indexing and relationships

## 🏗️ Technical Architecture

### **Data Structures & Algorithms**
- **Binary Search Tree** - Category hierarchy management
- **Enhanced ArrayList** - Transaction storage with O(1) lookups
- **Merge Sort & Quick Sort** - Efficient transaction sorting
- **Binary Search** - Fast data retrieval
- **Tree Traversal** - Category navigation algorithms

### **Design Patterns**
- **Service Layer Pattern** - Clean separation of business logic
- **MVC Architecture** - Model-View-Controller implementation
- **Singleton Pattern** - Database connection management
- **Observer Pattern** - UI updates and data synchronization

### **Database Design**
```sql
-- Optimized SQLite schema with proper relationships
Categories → Transactions (Foreign Key)
Categories → Budgets (Foreign Key)
Indexes on frequently queried columns
```

## 📁 Project Structure

```
MoneyMind/
├── src/
│   └── main.java.com/
│       └── moneymind/
│           ├── Main.java                 # Application entry point
│           ├── model/                    # Data models
│           │   ├── Transaction.java
│           │   ├── Category.java
│           │   ├── Budget.java
│           │   └── User.java
│           ├── datastructures/          # Custom data structures
│           │   ├── CategoryTree.java    # Binary Search Tree
│           │   ├── TransactionList.java # Enhanced ArrayList
│           │   └── TransactionSorter.java # Sorting algorithms
│           ├── database/                # Database layer
│           │   ├── DatabaseManager.java
│           │   └── DatabaseInitializer.java
│           ├── service/                 # Business logic
│           │   ├── TransactionService.java
│           │   ├── CategoryService.java
│           │   ├── BudgetService.java
│           │   └── ReportService.java
│           ├── ui/                      # User interface
│           │   ├── MainFrame.java
│           │   ├── TransactionPanel.java
│           │   ├── BudgetPanel.java
│           │   ├── CategoryPanel.java
│           │   └── ReportsPanel.java
│           └── utils/                   # Utility classes
│               ├── DateUtils.java
│               ├── CurrencyUtils.java
│               └── ValidationUtils.java
├── lib/
│   └── sqlite-jdbc-3.42.0.0.jar       # SQLite driver
├── data/
│   └── moneymind.db                    # Database file (auto-created)
└── README.md
```

## 🚀 Installation & Setup

### **Prerequisites**
- Java 8 or higher
- IDE (IntelliJ IDEA, Eclipse, or VS Code)
- 50MB free disk space

### **Step 1: Clone Repository**
```bash
git clone https://github.com/JxTIN21/MoneyMind.git
cd MoneyMind
```

### **Step 2: Download Dependencies**
1. Download SQLite JDBC driver: [sqlite-jdbc-3.42.0.0.jar](https://github.com/xerial/sqlite-jdbc/releases)
2. Place it in the `lib/` folder

### **Step 3: IDE Setup**

#### **IntelliJ IDEA**
1. Open project folder in IntelliJ
2. **File** → **Project Structure** → **Dependencies**
3. Add JAR: `lib/sqlite-jdbc-3.42.0.0.jar`
4. Run `Main.java`

#### **Eclipse**
1. Import as existing Java project
2. Right-click project → **Properties** → **Java Build Path**
3. **Add External JARs** → Select SQLite JAR
4. Run `Main.java`

#### **VS Code**
1. Install Java Extension Pack
2. Create `.vscode/settings.json`:
```json
{
    "java.project.sourcePaths": ["src"],
    "java.project.referencedLibraries": ["lib/**/*.jar"]
}
```

### **Step 4: Run Application**
```bash
# Compile
javac -cp "lib/*:src" -d build src/com/moneymind/*.java src/com/moneymind/*/*.java

# Run
java -cp "lib/*:build" com.moneymind.Main
```

## 📖 Usage Guide

### **Getting Started**
1. **Launch Application** - Run `Main.java` to start MoneyMind
2. **Add Categories** - Navigate to Categories tab and create your expense/income categories
3. **Record Transactions** - Start adding your financial transactions in the Transactions tab
4. **Set Budgets** - Create monthly or yearly budgets in the Budgets tab
5. **View Reports** - Analyze your financial health in the Reports tab

### **Key Features Walkthrough**

#### **Transaction Management**
- **Add Transaction**: Click "Add Transaction" and fill in details
- **Search & Filter**: Use the search bar or quick filter buttons
- **Sort Options**: Click column headers to sort by different criteria
- **Edit/Delete**: Select a transaction and use the respective buttons

#### **Budget Tracking**
- **Create Budget**: Set amount, period, and category
- **Monitor Progress**: Watch real-time usage percentages
- **Alerts**: Get notified when approaching or exceeding limits
- **Quick Setup**: Use predefined periods for faster budget creation

#### **Category Organization**
- **Tree Structure**: Organize categories in hierarchical format
- **Drag & Drop**: Reorganize categories easily (coming soon)
- **Context Menu**: Right-click for quick actions
- **Health Check**: Identify unused or orphaned categories

#### **Financial Reports**
- **Summary Reports**: Monthly/yearly financial overviews
- **Trend Analysis**: Track spending patterns over time
- **Category Breakdown**: See where your money goes
- **Health Score**: Get AI-powered financial health assessment

## 🧮 Algorithms & Data Structures

### **Implemented Algorithms**
- **Merge Sort** - O(n log n) stable sorting for transactions
- **Quick Sort** - O(n log n) average case sorting algorithm
- **Binary Search** - O(log n) search in sorted data
- **Tree Traversal** - In-order, pre-order, post-order for categories
- **Financial Health Scoring** - Custom algorithm for financial assessment

### **Data Structures**
- **Binary Search Tree** - Efficient category management
- **Enhanced ArrayList** - Transaction storage with HashMap indexing
- **TreeMap** - Date-based grouping and analysis
- **Priority Queue** - Budget alert system
- **Graph Traversal** - Category dependency resolution

## 🛠️ Advanced Features

### **Database Operations**
- ACID compliance with SQLite transactions
- Optimized queries with proper indexing
- Foreign key constraints for data integrity
- Automatic database migration and setup

### **Financial Analysis**
- **Expense Ratio Calculation** - Monitor spending vs income
- **Savings Rate Analysis** - Track financial health
- **Budget Variance Analysis** - Compare planned vs actual spending
- **Trend Prediction** - Forecast future spending patterns

### **UI/UX Enhancements**
- Custom table cell renderers for financial data
- Color-coded indicators for different transaction types
- Progress bars for budget utilization
- Context-sensitive menus and tooltips

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/AmazingFeature`)
3. **Commit** your changes (`git commit -m 'Add some AmazingFeature'`)
4. **Push** to the branch (`git push origin feature/AmazingFeature`)
5. **Open** a Pull Request

### **Development Guidelines**
- Follow Java naming conventions
- Add JavaDoc comments for public methods
- Include unit tests for new features
- Maintain the existing code structure

## 🧪 Testing

### **Manual Testing**
- Transaction CRUD operations
- Budget calculations and alerts
- Category tree operations
- Database connectivity

### **Future Testing Plans**
- JUnit integration for automated testing
- Integration tests for database operations
- UI testing with automated frameworks

## 🚧 Roadmap

### **Version 2.0 (Planned)**
- [ ] **Multi-currency Support** - Handle different currencies
- [ ] **Data Import/Export** - CSV, JSON, OFX formats
- [ ] **Cloud Synchronization** - Backup and sync across devices
- [ ] **Mobile App** - Android/iOS companion apps
- [ ] **Advanced Charts** - Interactive financial visualizations
- [ ] **Investment Tracking** - Stock and portfolio management
- [ ] **Bill Reminders** - Automated payment notifications
- [ ] **Multiple Accounts** - Bank account management

### **Technical Improvements**
- [ ] **Unit Testing Suite** - Comprehensive test coverage
- [ ] **Performance Optimization** - Large dataset handling
- [ ] **Security Enhancements** - Data encryption and authentication
- [ ] **Plugin Architecture** - Extensible feature system

## 📚 Learning Outcomes

This project demonstrates proficiency in:

### **Core Computer Science Concepts**
- ✅ **Data Structures** - Trees, Lists, Maps, Queues
- ✅ **Algorithms** - Sorting, Searching, Tree Traversal
- ✅ **Database Design** - Normalization, Indexing, Relationships
- ✅ **Object-Oriented Programming** - Inheritance, Polymorphism, Encapsulation

### **Software Engineering Practices**
- ✅ **Design Patterns** - Singleton, Observer, MVC
- ✅ **Code Organization** - Package structure, Separation of concerns
- ✅ **Error Handling** - Exception management, User feedback
- ✅ **Documentation** - JavaDoc, README, Code comments

### **Practical Development Skills**
- ✅ **GUI Development** - Swing components, Event handling
- ✅ **Database Integration** - JDBC, SQL queries, Transactions
- ✅ **File I/O Operations** - Data persistence, Configuration
- ✅ **Testing & Debugging** - Problem diagnosis, Solution implementation

## 🐛 Known Issues

- **Large Dataset Performance** - May slow down with >10,000 transactions
- **Memory Usage** - Tree structure keeps all categories in memory
- **Date Formatting** - Limited to system locale settings

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Jatin Srivastava** - *Initial work* - [YourGitHub](https://github.com/JxTIN21)

## 🙏 Acknowledgments

- **SQLite Team** - For the excellent embedded database
- **Java Community** - For comprehensive documentation and support
- **Open Source Contributors** - For inspiration and best practices

## 📞 Support

If you encounter any issues or have questions:

1. **Check Issues** - Search existing GitHub issues
2. **Create Issue** - Report bugs or request features
3. **Discussion** - Start a discussion for general questions

---

⭐ **Star this repository if you find it helpful!**

*MoneyMind - Take control of your finances with intelligent tracking and analysis.*