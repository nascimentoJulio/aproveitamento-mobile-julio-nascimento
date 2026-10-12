# Minhas Tarefas (Task Organizer) 📝📱

**Trabalho Prático — Avaliação para Aproveitamento de Estudos**  
**Disciplina:** Desenvolvimento de Aplicações para Dispositivos Móveis  
**Professora:** Manoella Jarces de Azevedo  
**Estudante:** Júlio Nascimento  
**Repositório:** `aproveitamento-mobile-julio-nascimento`

---

## 📌 Descrição do Aplicativo

O **Minhas Tarefas (Task Organizer)** é um gerenciador pessoal de tarefas desenvolvido como aplicativo Android nativo em **Java**. O projeto implementa um **CRUD completo** (Criar, Listar, Editar e Excluir) utilizando a arquitetura **MVVM** e persistência local com **Room Database**, além de validações de formulário robustas (padrão **Android Saripaar**) e agendamento de lembretes com **NotificationChannel** e **AlarmManager**.

---

## ✨ Requisitos Funcionais Implementados

### 1. Entrada de Dados & Formulário
- **Título (`EditText`)**: Campo obrigatório, mínimo de 3 e máximo de 50 caracteres (espaços nas extremidades são ignorados).
- **Descrição (`EditText` multilinha)**: Campo opcional, máximo de 200 caracteres.
- **Data Limite (`Button` + `DatePickerDialog` + `TimePickerDialog`)**: Seleção de data e hora em fluxo contínuo. Não permite selecionar datas anteriores à data atual no cadastro.
- **Prioridade (`Spinner`)**: Escolha obrigatória (`Baixa`, `Média` ou `Alta`), bloqueando a seleção do item genérico de instrução.
- **Categoria (`Spinner`)**: Escolha obrigatória (`Estudos`, `Trabalho`, `Pessoal` ou `Outros`), bloqueando a seleção do item genérico.
- **Concluída (`CheckBox`)**: Marca o estado de conclusão da tarefa (padrão desmarcado).
- **Lembrete (`CheckBox`)**: Indica se uma notificação deve ser agendada na data/hora limite.

### 2. Validação de Formulários (Padrão Android Saripaar)
- Validação executada ao acionar o botão **Salvar** antes de qualquer gravação no banco de dados.
- Sinalização visual diretamente no campo (`setError()`) para títulos e descrições inválidos.
- Exibição de alertas via `Toast` para erros em componentes de seleção (`Spinner`).
- Mapeamento de múltiplos erros simultâneos e redirecionamento de foco para o primeiro campo inválido.

### 3. Persistência Local em Banco de Dados (CRUD com Room)
- **DAO & Database**: Acesso ao banco SQLite isolado com a biblioteca **Room** (`TaskDao`, `TaskDatabase`, `TaskRepository`).
- **Inserção**: Cadastra novas tarefas com id autogerado.
- **Listagem**: Exibe as tarefas em um `RecyclerView` dinâmico contendo Título, Data Limite, Categoria e Prioridade. Atualização automática em tempo real com `LiveData`.
- **Alteração**: Toque no item para abrir o formulário preenchido e atualizar o registro existente.
- **Remoção**: Botão de exclusão com diálogo de confirmação (**`AlertDialog`**).
- **Estado Vazio (`Empty State`)**: Exibe mensagem clara quando nenhuma tarefa está cadastrada.

### 4. Notificações do Sistema
- **`NotificationChannel`**: Canal de notificação dedicado (`Task Reminders`) configurado com alta prioridade, som e vibração.
- **Permissões**: Solicitação de `POST_NOTIFICATIONS` em tempo de execução para Android 13+ (API 33+).
- **Agendamento Exato**: Uso de `AlarmManager.setAlarmClock()` para garantir o disparo exato do lembrete, mesmo em modo de economia de energia (*Doze Mode*).
- **Abertura do App**: Notificação associada a um `PendingIntent` que abre o aplicativo diretamente ao ser tocada.

---

## 🛠️ Tecnologias e Requisitos Técnicos

- **Linguagem**: Java (Compatibilidade Java 11)
- **IDE**: Android Studio 2026.1.4
- **SDK Mínimo (`minSdk`)**: API 24 (Android 7.0 Nougat)
- **SDK Alvo (`targetSdk`)**: API 37
- **Interface**: Layouts XML nativos Material Components (sem Jetpack Compose)
- **Arquitetura**: MVVM (Model-View-ViewModel) + Repository + LiveData
- **Internacionalização**: Todas as strings da interface organizadas em `strings.xml`
- **Tema Visual**: Interface moderna em tom escuro (*Dark Theme*) nas cores **Preto e Verde Esmeralda**.

---

## 🚀 Como Executar o Projeto

1. **Clonar o Repositório**:
   ```bash
   git clone https://github.com/julionascimento/aproveitamento-mobile-julio-nascimento.git
   ```

2. **Abrir no Android Studio**:
   - Abra o **Android Studio (2026.1.4 ou superior)**.
   - Selecione **File > Open** e escolha a pasta raiz do projeto.

3. **Sincronizar o Gradle**:
   - Aguarde o Android Studio realizar o **Gradle Sync** automático de todas as dependências (Room, AppCompat, Material Components).

4. **Executar a Aplicação**:
   - Selecione um emulador (API 24+) ou dispositivo físico com a depuração USB ativada.
   - Clique no botão **Run** (`Shift + F10` ou botão `▶`).

---

## 📁 Estrutura do Código Fonte

```
app/src/main/java/br/edu/com/ifsul/taskorganizer/
├── model/           # Entidades (Task, Priority, Category)
├── data/            # Room Database, DAO e TypeConverters
├── repository/      # TaskRepository (Manipulação assíncrona do banco)
├── viewmodel/       # TaskViewModel (Ponte entre UI e Repository com LiveData)
├── notification/   # NotificationHelper e TaskNotificationReceiver (AlarmManager)
├── saripaar/        # Framework de Validação (Validator, ValidationError, Annotations)
├── ui/              # Activities (MainActivity, AddEditTaskActivity) e TaskAdapter
└── MainActivity.java
```
