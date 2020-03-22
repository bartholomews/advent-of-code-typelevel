package advent.solutions

import scala.util.Try

/** Day 2: 1202 Program Alarm
  *
  * @see https://adventofcode.com/2019/day/2
  */
object Day2 {

  object Part1 {

    type Result[A] = Either[Error, A]

    type Op = (Int, Int) => Int

    /** Runs an Intcode program
      *
      * @param program A list of opcodes and positions
      * @return The program after having been run on itself
      */
    def run(
        program: List[Int]
    ): Either[Error.InvalidProgramError, List[Int]] = {
      val start: Int = 0
      val step: Int = 4
      val indexList = List.range(start, program.length, step)
      val p0: Result[List[Int]] = Right(program)
      val result = indexList.foldLeft(p0)((pOrError, opcodeIndex) =>
        pOrError.flatMap(runOp(_, opcodeIndex))
      )
      result.left.flatMap {
        case Error.Terminate(p)           => Right(p)
        case e: Error.InvalidProgramError => Left(e)
      }
    }

    /**
      * Any error that could occur when running a program.
      *
      * All errors will halt program execution
      */
    sealed trait Error

    object Error {

      /** The user supplied an invalid program */
      sealed trait InvalidProgramError extends Error

      object InvalidProgramError {

        /** A program contained an unrecognized opcode */
        final case class UnrecognizedCode(index: Int, code: Int)
            extends InvalidProgramError

        /** The program attempted to lookup or store a value at an index exceeding the program length */
        final case class IndexNotFound(index: Int) extends InvalidProgramError
      }

      /** The program encountered a termination opcode and terminated */
      final case class Terminate(program: List[Int]) extends Error
    }

    private val additionCode: Int = 1
    private val multiplicationCode: Int = 2
    private val terminationCode: Int = 99

    /** Get and run a single operation on the program
      *
      * @param program      The program
      * @param opcodeIndex  The index of the operation to run.
      *                     The opcode is located at `opcodeIndex`.
      *                     The index of the first value to operate on is located at `opcodeIndex + 1`.
      *                     The index of the second value to operate on is located at `opcodeIndex + 2`.
      *                     The index to store the result at is located at `opcodeIndex + 3`.
      */
    private def runOp(
        program: List[Int],
        opcodeIndex: Int
    ): Result[List[Int]] = {
      lookupOp(program, opcodeIndex)
        .flatMap(operate(opcodeIndex, program, _))
    }

    /** Get the operation to run on the program
      *
      * @param program      The program
      * @param opcodeIndex  The index where the opcode is located.
      * @return             A multiplication or addition function corresponding to the opcode
                            Or an error in the case of termination or an unrecognized opcode
      */
    private def lookupOp(
        program: List[Int],
        opcodeIndex: Int
    ): Result[Op] = {
      val opcode = unsafeLookup(program, opcodeIndex)
      opcode match {
        case `additionCode` => Right(_ + _)
        case `multiplicationCode` =>
          Right(_ * _)
        case `terminationCode` => Left(Error.Terminate(program))
        case unrecognizedCode =>
          Left(
            Error.InvalidProgramError
              .UnrecognizedCode(opcodeIndex, unrecognizedCode)
          )
      }
    }

    /** Run an operation on a program
      *
      * @param program      The program
      * @param opcodeIndex  The index where the opcode is located.
      * @param op           The function corresponding to the operation
      * @return             A modified program
      */
    private def operate(
        opcodeIndex: Int,
        program: List[Int],
        op: Op
    ): Result[List[Int]] = {
      val xIndex = unsafeLookup(program, opcodeIndex + 1)
      val yIndex = unsafeLookup(program, opcodeIndex + 2)
      val storeIndex = unsafeLookup(program, opcodeIndex + 3)
      for {
        x <- attemptLookup(program, xIndex)
        y <- attemptLookup(program, yIndex)
        p <- attemptStore(program, storeIndex, op(x, y))
      } yield p
    }

    /** Gets the value at an index.  This should only be used when the index is proven to exist within the program */
    private def unsafeLookup(program: List[Int], i: Int): Int = {
      program(i)
    }

    /** Gets the value at an index.  This returns an [[IndexNotFound]] error if the index does not exist. */
    private def attemptLookup(program: List[Int], i: Int): Result[Int] = {
      program.lift(i).toRight(Error.InvalidProgramError.IndexNotFound(i))
    }

    /** Stores a value at an index.  This returns an [[IndexNotFound]] error if the index does not exist. */
    private def attemptStore(
        program: List[Int],
        i: Int,
        v: Int
    ): Result[List[Int]] = {
      Try(program.updated(i, v)).toEither.left.map(_ =>
        Error.InvalidProgramError.IndexNotFound(i)
      )
    }
  }

  object Part2 {

    /** Represents the two numbers provided at addresses 1 and 2 of an Intcode program */
    final case class Input(noun: Int, verb: Int)

    /** Calculates the input required to produce a given output
      *
      * @param program A list of opcodes and positions.  The positions at address 1 and 2 will be replaced with input
      * @param output  The given output of the program
      * @return The input that would be entered in the program to produce the given output
      */
    def inputForOutput(program: List[Int], output: Int): Option[Input] = {
      ???
    }

    // Uncomment this to solve part 2
    // /** Stores the noun and verb at addresses 1 and 2 of the program */
    // private def setInput[P](input: Input, program: P)(
    //     implicit I: Index[P, Int, Int]
    // ): P = {
    //   ???
    // }
  }

  // scalastyle:off
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  def main(args: Array[String]): Unit = {

    // Copy the puzzle input from https://adventofcode.com/2019/day/2/input
    // Solve your puzzle using the functions in parts 1 and 2
  }
  // scalastyle:on
}
