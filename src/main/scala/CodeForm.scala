import java.awt.{FlowLayout, Font, BorderLayout}
import java.awt.event._
import javax.swing._
import scala.tools.reflect.ToolBox
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe
import scala.util.{Failure, Success, Try}


class CodeForm extends JFrame {

  val txtCode = new JTextArea
  val txtAST = new JTextArea
  val bGetAST = new JButton("Get AST")
  val statusBar = new JPanel

  val toolbox = currentMirror.mkToolBox()

  val (keyStrokePE, indicatorPE) = getKeyStrokeForProcessExpr
  val (keyStrokeSE, indicatorSE) = getKeyStrokeForSelectExpr

  {
    setTitle("Scala Code to AST")
    val font = new Font("Courier", Font.PLAIN, 12)
    txtCode.setFont(font)
    txtAST.setFont(font)
    txtAST.setLineWrap(true)
    val codeScrollPane = new JScrollPane(txtCode)
    val astScrollPane = new JScrollPane(txtAST)
    val splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codeScrollPane, astScrollPane)
    splitPane.setDividerLocation(200)
    val toolbar = new JToolBar()
    toolbar.setFloatable(false)
    toolbar.add(new JLabel(s"Press $indicatorPE to "))
    toolbar.add(bGetAST)
    toolbar.add(new JLabel(s", $indicatorSE to focus expression."))
    setLayout(new BorderLayout)
    add(toolbar, BorderLayout.NORTH)
    add(splitPane, BorderLayout.CENTER)
    setSize(600, 400)
  }

  bGetAST.addActionListener(new ActionListener {
    override def actionPerformed(ev: ActionEvent) = processExpression()
  })

  this.getRootPane.getActionMap.put("Process Expression", new AbstractAction {
    override def actionPerformed(e: ActionEvent) = processExpression()
  })

  this.getRootPane.getActionMap.put("Select Expression", new AbstractAction {
    override def actionPerformed(e: ActionEvent) = txtCode.grabFocus()
  })

  this.getRootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokePE, "Process Expression")
  this.getRootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeSE, "Select Expression")

  txtAST.addFocusListener(new FocusAdapter {
    override def focusGained(e: FocusEvent) {
      SwingUtilities.invokeLater(new Runnable {
        override def run() {
          txtAST.selectAll()
        }
      })
    }
  })

  def processExpression() {
    Try(toolbox.parse(txtCode.getText)) match {
      case Success(tree) =>
        txtAST.setText(universe.showRaw(tree))
        txtAST.grabFocus()
      case Failure(ex) => txtAST.setText(ex.getMessage)
    }
  }

  protected def getKeyStrokeForProcessExpr =
    Option(System.getProperty("os.name")) match {
      case Some(prop) if prop.toLowerCase.startsWith("mac os x") =>
        (KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.META_DOWN_MASK), "⌘G")
      case _ =>
        (KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK), "Ctrl-G")
    }

  protected def getKeyStrokeForSelectExpr =
    Option(System.getProperty("os.name")) match {
      case Some(prop) if prop.toLowerCase.startsWith("mac os x") =>
        (KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.META_DOWN_MASK), "⌘T")
      case _ =>
        (KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK), "Ctrl-T")
    }

  setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
}
