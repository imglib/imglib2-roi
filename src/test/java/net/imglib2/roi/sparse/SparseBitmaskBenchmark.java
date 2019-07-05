package net.imglib2.roi.sparse;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.img.sparse.NtreeImg;
import net.imglib2.img.sparse.NtreeImgFactory;
import net.imglib2.roi.sparse.labkit.SparseIterableRegion;
import net.imglib2.type.logic.NativeBoolType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@State( Scope.Benchmark )
public class SparseBitmaskBenchmark
{
	@Benchmark
	public void benchmark() {
		SparseBitmask image = new SparseBitmask( 3 );
		fillHypersphere( image, 50, 50, 50, 50 );
	}

	@Benchmark
	public void benchmarkNDTree() {
		NtreeImg< UnsignedByteType, ? > image = new NtreeImgFactory<>( new UnsignedByteType() ).create( 100, 100, 100 );
		fillHypersphere( image, 50, 50, 50, 50 );
	}

	@Benchmark
	public void labkitSparse() {
		SparseIterableRegion image = new SparseIterableRegion( new FinalInterval( 100, 100, 100 ) );
		fillHypersphere( image, 50, 50, 50, 50 );
	}

	SparseBitmask sphere = initSphere();

	private SparseBitmask initSphere()
	{
		SparseBitmask image = new SparseBitmask( 3 );
		fillHypersphere( image, 50, 50, 50, 50 );
		return image;
	}

	@Benchmark
	public void read(Blackhole bh) throws InterruptedException
	{
		Runnable action = () -> {
			long sum = 0;
			for(NativeBoolType pixel : Views.interval(sphere, new FinalInterval( 100, 100, 100 ) ))
				if(pixel.get()) sum++;
			bh.consume( sum );
		};
		executeInParallel( action, 8 );
	}

	private void executeInParallel( Runnable action, int numberOfThreads ) throws InterruptedException
	{
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		List<Callable<Void>> tasks = IntStream.range( 0, numberOfThreads ).mapToObj( ignore -> (Callable<Void>) () -> { action.run(); return null; } ).collect( Collectors.toList() );
		executorService.invokeAll( tasks );
		executorService.shutdown();
	}

	public static void main( final String... args ) throws RunnerException
	{
		final Options opt = new OptionsBuilder()
				.include( SparseBitmaskBenchmark.class.getSimpleName() )
				.forks( 0 )
				.warmupIterations( 4 )
				.measurementIterations( 8 )
				.warmupTime( TimeValue.milliseconds( 100 ) )
				.measurementTime( TimeValue.milliseconds( 100 ) )
				.build();
		new Runner( opt ).run();
	}

	private static void fillHypersphere( RandomAccessible< ? extends NumericType<?> > img, long radius, long... position )
	{
		RandomAccess< ? extends Neighborhood< ? extends NumericType< ? > > > a = new HyperSphereShape( radius ).neighborhoodsRandomAccessible( img ).randomAccess();
		a.setPosition( position );
		a.get().forEach( t -> t.setOne() );
	}
}
