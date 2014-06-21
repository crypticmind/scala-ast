import java.awt.{Font, BorderLayout}
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing._
import scala.tools.reflect.ToolBox
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe
import scala.util.{Failure, Success, Try}


class CodeForm extends JFrame {

  val txtCode = new JTextArea
  val txtAST = new JTextArea
  val bGetAST = new JButton("Get AST")

  val toolbox = currentMirror.mkToolBox()

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
    toolbar.add(bGetAST)
    setLayout(new BorderLayout)
    add(toolbar, BorderLayout.NORTH)
    add(splitPane, BorderLayout.CENTER)
    setSize(600, 400)
  }

  bGetAST.addActionListener(new ActionListener {
    override def actionPerformed(ev: ActionEvent) {
      Try(toolbox.parse(txtCode.getText)) match {
        case Success(tree) =>
          txtAST.setText(universe.showRaw(tree))
          txtAST.selectAll()
        case Failure(ex) => txtAST.setText(ex.getMessage)
      }
    }
  })
}
