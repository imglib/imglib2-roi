package net.imglib2.roi.labeling;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.IntType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import static junit.framework.TestCase.assertFalse;

@State( value = Scope.Benchmark )
public class ImgLabelingBenchmark
{
	Img<IntType > indexImg = ArrayImgs.ints(100, 100, 100);
	ImgLabeling<String, ?> imgLabeling = new ImgLabeling<>( indexImg );

	@Benchmark
	public void benchmarkCursor( Blackhole bh ) {
		Cursor< LabelingType< String > > cursor = imgLabeling.cursor();
		while( cursor.hasNext() )
			bh.consume( cursor.next() );
	}

	@Benchmark
	public void benchmarkRandomAccess( Blackhole bh ) {
		RandomAccess< LabelingType< String > > randomAccess = imgLabeling.randomAccess();
		Cursor< ? > cursor = indexImg.localizingCursor();
		while( cursor.hasNext() )
		{
			cursor.fwd();
			randomAccess.setPosition( cursor );
			bh.consume( randomAccess.get() );
		}
	}

	public static void main( String... args ) throws RunnerException
	{
		final Options opt = new OptionsBuilder()
				.include( ImgLabelingBenchmark.class.getSimpleName() )
				.forks( 0 )
				.warmupIterations( 4 )
				.measurementIterations( 8 )
				.warmupTime( TimeValue.milliseconds( 100 ) )
				.measurementTime( TimeValue.milliseconds( 100 ) )
				.build();
		new Runner( opt ).run();
	}
}
